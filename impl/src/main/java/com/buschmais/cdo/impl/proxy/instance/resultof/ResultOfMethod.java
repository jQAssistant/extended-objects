package com.buschmais.cdo.impl.proxy.instance.resultof;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.api.annotation.ResultOf;
import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.query.CdoQueryImpl;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.spi.metadata.ResultOfMethodMetadata;

import java.util.Collections;
import java.util.List;

public class ResultOfMethod<Entity> implements ProxyMethod<Entity> {

    private ResultOfMethodMetadata resultOfMethodMetadata;
    private InstanceManager instanceManager;
    private DatastoreSession datastoreSession;

    public ResultOfMethod(ResultOfMethodMetadata resultOfMethodMetadata, InstanceManager instanceManager, DatastoreSession datastoreSession) {
        this.resultOfMethodMetadata = resultOfMethodMetadata;
        this.instanceManager = instanceManager;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        CdoQueryImpl<Class<?>> query = new CdoQueryImpl(resultOfMethodMetadata.getQuery(), datastoreSession, instanceManager, Collections.<Class<?>>emptyList());
        String usingThisAs = resultOfMethodMetadata.getUsingThisAs();
        query.withParameter(usingThisAs, instanceManager.getInstance(entity));
        List<ResultOf.Parameter> parameters = resultOfMethodMetadata.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            query.withParameter(parameters.get(i).value(), args[i]);
        }
        Query.Result<Object> result = query.execute();
        if (resultOfMethodMetadata.isSingleResult()) {
            return result.hasResult() ? result.getSingleResult() : null;
        }
        return result;
    }
}
