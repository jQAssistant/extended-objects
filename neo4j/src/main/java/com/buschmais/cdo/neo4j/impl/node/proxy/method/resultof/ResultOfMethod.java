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

public class ResultOfMethod implements NodeProxyMethod {

    private ResultOfMethodMetadata resultOfMethodMetadata;

    private InstanceManager instanceManager;

    private GraphDatabaseService graphDatabaseService;

    private ResultOf.Parameter[] parameters;

    public ResultOfMethod(ResultOfMethodMetadata resultOfMethodMetadata, InstanceManager instanceManager, GraphDatabaseService graphDatabaseService) {
        this.resultOfMethodMetadata = resultOfMethodMetadata;
        this.instanceManager = instanceManager;
        this.graphDatabaseService = graphDatabaseService;
        Method method = resultOfMethodMetadata.getBeanMethod().getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        parameters = new ResultOf.Parameter[parameterAnnotations.length];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            ResultOf.Parameter parameter = null;
            for (Annotation annotation : parameterAnnotations[i]) {
                if (ResultOf.Parameter.class.equals(annotation.annotationType())) {
                    parameter = (ResultOf.Parameter) annotation;
                }
            }
            if (parameter == null) {
                throw new CdoException("Cannot determine parameter names for '" + method.getName() + "', all parameters must be annotated with '" + ResultOf.Parameter.class.getName() + "'.");
            }
            parameters[i] = parameter;
        }
    }

    @Override
    public Object invoke(Node element, Object instance, Object[] args) {
        CypherTypeQueryImpl query = new CypherTypeQueryImpl(resultOfMethodMetadata.getQuery(), new ExecutionEngine(graphDatabaseService), instanceManager, Collections.<Class<?>>emptyList());
        String usingThisAs = resultOfMethodMetadata.getUsingThisAs();
        if (usingThisAs != null) {
            query.withParameter(usingThisAs, instanceManager.getNode(instance));
        }
        for (int i = 0; i < parameters.length; i++) {
            Object parameterValue = args[i];
            if (instanceManager.isNode(parameterValue)) {
                parameterValue = instanceManager.getNode(parameterValue);
            }
            query.withParameter(parameters[i].value(), parameterValue);
        }
        return query.execute();
    }
}
