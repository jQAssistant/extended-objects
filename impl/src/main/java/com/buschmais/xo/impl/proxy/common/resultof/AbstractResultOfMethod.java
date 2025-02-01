package com.buschmais.xo.impl.proxy.common.resultof;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.metadata.method.ResultOfMethodMetadata;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.query.XOQueryImpl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Abstract base implementation for ResultOf methods.
 *
 * @param <DatastoreType>
 *     The datastore type to be used as "this" instance.
 * @param <Entity>
 *     The entity type.
 * @param <Relation>
 *     The relation type.
 */
public abstract class AbstractResultOfMethod<DatastoreType, Entity, Relation> implements ProxyMethod<DatastoreType> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;
    private final ResultOfMethodMetadata<?> resultOfMethodMetadata;

    public AbstractResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        this.sessionContext = sessionContext;
        this.resultOfMethodMetadata = resultOfMethodMetadata;
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        XOQueryImpl<?, ?, AnnotatedElement, ?, ?> query = new XOQueryImpl<>(sessionContext, resultOfMethodMetadata.getQuery(),
            resultOfMethodMetadata.getRowType());
        Object thisInstance = getThisInstance(datastoreType, sessionContext);
        if (thisInstance != null) {
            String usingThisAs = resultOfMethodMetadata.getUsingThisAs();
            query.withParameter(usingThisAs, thisInstance);
        }
        List<ResultOfMethodMetadata.QueryParameter> parameters = resultOfMethodMetadata.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            query.withParameter(parameters.get(i)
                .getName(), args[i]);
        }
        Query.Result<?> result = query.execute();
        Class<?> returnType = resultOfMethodMetadata.getReturnType();
        if (void.class.equals(returnType)) {
            result.close();
        } else if (Stream.class.isAssignableFrom(returnType)) {
            return result.asStream();
        } else if (Iterable.class.isAssignableFrom(returnType)) {
            if (Query.Result.class.isAssignableFrom(returnType)) {
                return result;
            } else if (List.class.equals(returnType)) {
                return result.asStream()
                    .collect(toList());
            } else if (Set.class.equals(returnType)) {
                return result.asStream()
                    .collect(toSet());
            }
        } else {
            return result.hasResult() ? result.getSingleResult() : null;
        }
        return null;
    }

    protected abstract Object getThisInstance(DatastoreType datastoreType, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext);

}
