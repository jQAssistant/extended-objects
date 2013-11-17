package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.neo4j.impl.node.InstanceManager;
import org.neo4j.cypher.javacompat.ExecutionEngine;

import java.util.List;

public class CypherStringQueryImpl extends AbstractCypherQueryImpl<String> {

    public CypherStringQueryImpl(String expression, ExecutionEngine executionEngine, InstanceManager instanceManager, List<Class<?>> types) {
        super(expression, executionEngine, instanceManager, types);
    }

    @Override
    protected String getQuery() {
        return getExpression();
    }

    @Override
    protected List<Class<?>> getResultTypes(String expression, List<Class<?>> types) {
        return types;
    }
}
