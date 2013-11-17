package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.cypher.javacompat.ExecutionEngine;

public class CypherStringQueryImpl extends AbstractCypherQueryImpl<String> {

    public CypherStringQueryImpl(String expression, ExecutionEngine executionEngine, InstanceManager instanceManager) {
        super(expression, executionEngine, instanceManager);
    }

    @Override
    protected String getQuery() {
        return getExpression();
    }


}
