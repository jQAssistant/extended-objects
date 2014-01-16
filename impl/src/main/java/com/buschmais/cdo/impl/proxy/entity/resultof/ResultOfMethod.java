package com.buschmais.cdo.impl.proxy.entity.resultof;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.query.CdoQueryImpl;
import com.buschmais.cdo.spi.metadata.method.ResultOfMethodMetadata;

import java.util.Collections;
import java.util.List;

public class ResultOfMethod<Entity> implements ProxyMethod<Entity> {

    private SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;
    private ResultOfMethodMetadata<?> resultOfMethodMetadata;

    public ResultOfMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext, ResultOfMethodMetadata<?> resultOfMethodMetadata) {
        this.sessionContext = sessionContext;
        this.resultOfMethodMetadata = resultOfMethodMetadata;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        CdoQueryImpl<?, Class<?>, Entity, ?> query = (CdoQueryImpl<?, Class<?>, Entity, ?>) new CdoQueryImpl<>(sessionContext, resultOfMethodMetadata.getQuery(), Collections.<Class<?>>emptyList());
        String usingThisAs = resultOfMethodMetadata.getUsingThisAs();
        query.withParameter(usingThisAs, sessionContext.getEntityInstanceManager().getInstance(entity));
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
}
