package com.buschmais.cdo.neo4j.impl.node.proxy.method.resultof;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ResultOfMethodMetadata;
import com.buschmais.cdo.neo4j.impl.query.CdoQueryImpl;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.spi.proxy.ProxyMethod;

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
        query.withParameter(usingThisAs, instance);
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
