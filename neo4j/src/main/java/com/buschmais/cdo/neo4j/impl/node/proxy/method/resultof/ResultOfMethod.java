package com.buschmais.cdo.neo4j.impl.node.proxy.method.resultof;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.api.annotation.ResultOf;
import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import com.buschmais.cdo.neo4j.impl.node.metadata.ResultOfMethodMetadata;
import com.buschmais.cdo.neo4j.impl.query.CypherTypeQueryImpl;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class ResultOfMethod implements NodeProxyMethod {

    private ResultOfMethodMetadata resultOfMethodMetadata;
    private InstanceManager instanceManager;
    private GraphDatabaseService graphDatabaseService;

    public ResultOfMethod(ResultOfMethodMetadata resultOfMethodMetadata, InstanceManager instanceManager, GraphDatabaseService graphDatabaseService) {
        this.resultOfMethodMetadata = resultOfMethodMetadata;
        this.instanceManager = instanceManager;
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public Object invoke(Node element, Object instance, Object[] args) {
        CypherTypeQueryImpl query = new CypherTypeQueryImpl(resultOfMethodMetadata.getQuery(), new ExecutionEngine(graphDatabaseService), instanceManager, Collections.<Class<?>>emptyList());
        String usingThisAs = resultOfMethodMetadata.getUsingThisAs();
        query.withParameter(usingThisAs, instanceManager.getNode(instance));
        List<ResultOf.Parameter> parameters = resultOfMethodMetadata.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            Object parameterValue = args[i];
            if (instanceManager.isNode(parameterValue)) {
                parameterValue = instanceManager.getNode(parameterValue);
            }
            query.withParameter(parameters.get(i).value(), parameterValue);
        }
        return query.execute();
    }
}
