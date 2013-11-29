package com.buschmais.cdo.neo4j.impl.node.proxy.method.resultof;

import com.buschmais.cdo.api.Query;
import com.buschmais.cdo.neo4j.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ResultOfMethodMetadata;
import com.buschmais.cdo.neo4j.impl.query.CypherTypeQueryImpl;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import org.neo4j.graphdb.Node;

import java.util.Collections;
import java.util.List;

public class ResultOfMethod implements NodeProxyMethod {

    private ResultOfMethodMetadata resultOfMethodMetadata;
    private InstanceManager instanceManager;
    private DatastoreSession datastoreSession;

    public ResultOfMethod(ResultOfMethodMetadata resultOfMethodMetadata, InstanceManager instanceManager, DatastoreSession datastoreSession) {
        this.resultOfMethodMetadata = resultOfMethodMetadata;
        this.instanceManager = instanceManager;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        CypherTypeQueryImpl query = new CypherTypeQueryImpl(resultOfMethodMetadata.getQuery(), datastoreSession, instanceManager, Collections.<Class<?>>emptyList());
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
