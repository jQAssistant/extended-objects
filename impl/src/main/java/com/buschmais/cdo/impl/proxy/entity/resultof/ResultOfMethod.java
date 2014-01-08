package com.buschmais.cdo.impl.proxy.entity.resultof;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.query.CdoQueryImpl;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.metadata.method.ResultOfMethodMetadata;

import java.util.Collections;
import java.util.List;

public class ResultOfMethod<Entity> implements ProxyMethod<Entity> {

    private ResultOfMethodMetadata resultOfMethodMetadata;
    private InstanceManager instanceManager;
    private CdoTransaction cdoTransaction;
    private InterceptorFactory interceptorFactory;
    private DatastoreSession datastoreSession;

    public ResultOfMethod(ResultOfMethodMetadata resultOfMethodMetadata, InstanceManager instanceManager, CdoTransaction cdoTransaction, InterceptorFactory interceptorFactory, DatastoreSession datastoreSession) {
        this.resultOfMethodMetadata = resultOfMethodMetadata;
        this.instanceManager = instanceManager;
        this.cdoTransaction = cdoTransaction;
        this.interceptorFactory = interceptorFactory;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        CdoQueryImpl<?, Class<?>> query = new CdoQueryImpl(resultOfMethodMetadata.getQuery(), datastoreSession, instanceManager, cdoTransaction, interceptorFactory, Collections.<Class<?>>emptyList());
        String usingThisAs = resultOfMethodMetadata.getUsingThisAs();
        query.withParameter(usingThisAs, instanceManager.getEntityInstance(entity));
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
