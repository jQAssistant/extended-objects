package com.buschmais.xo.impl.proxy.common.resultof;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import com.buschmais.xo.api.Query;
import com.buschmais.xo.api.metadata.method.ResultOfMethodMetadata;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.converter.ValueConverter;
import com.buschmais.xo.impl.query.XOQueryImpl;

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
        Class<?> returnType = resultOfMethodMetadata.getReturnType();
        XOQueryImpl<?, ?, AnnotatedElement, ?, ?> query = new XOQueryImpl<>(sessionContext, resultOfMethodMetadata.getQuery(), returnType);
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
        if (void.class.equals(returnType)) {
            result.close();
            return null;
        } else if (resultOfMethodMetadata.isSingleResult()) {
            if (result.hasResult()) {
                return ValueConverter.convert(result.getSingleResult(), returnType, sessionContext);
            }
            return null;
        }
        return result;
    }

    protected abstract Object getThisInstance(DatastoreType datastoreType, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext);

}
