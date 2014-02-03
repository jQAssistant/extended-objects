package com.buschmais.cdo.impl.proxy.common.resultof;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.query.CdoQueryImpl;
import com.buschmais.cdo.spi.metadata.method.ResultOfMethodMetadata;

import java.util.Collections;
import java.util.List;

public abstract class AbstractResultOfMethod<DatastoreType, Entity, Relation> implements ProxyMethod<DatastoreType> {

    private SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext;
    private ResultOfMethodMetadata<?> resultOfMethodMetadata;

    public AbstractResultOfMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        this.sessionContext = sessionContext;
        this.resultOfMethodMetadata = resultOfMethodMetadata;
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        CdoQueryImpl<?, Class<?>, ?, ?> query = (CdoQueryImpl<?, Class<?>, ?, ?>) new CdoQueryImpl<>(sessionContext, resultOfMethodMetadata.getQuery(), Collections.<Class<?>>emptyList());
        String usingThisAs = resultOfMethodMetadata.getUsingThisAs();
        query.withParameter(usingThisAs, getInstanceManager(sessionContext).getInstance(datastoreType));
        List<ResultOf.Parameter> parameters = resultOfMethodMetadata.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            query.withParameter(parameters.get(i).value(), args[i]);
        }
        Query.Result<?> result = query.execute();
        if (resultOfMethodMetadata.isSingleResult()) {
            return result.hasResult() ? result.getSingleResult() : null;
        }
        return result;
    }

    protected abstract AbstractInstanceManager<?, DatastoreType> getInstanceManager(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext);
}
