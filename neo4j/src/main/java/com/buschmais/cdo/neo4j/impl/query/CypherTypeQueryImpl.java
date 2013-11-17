package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.api.annotation.Cypher;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.cypher.javacompat.ExecutionEngine;

public class CypherTypeQueryImpl extends AbstractCypherQueryImpl<Class<?>> {

    public CypherTypeQueryImpl(Class<?> expression, ExecutionEngine executionEngine, InstanceManager instanceManager) {
        super(expression, executionEngine, instanceManager);
    }

    @Override
    protected String getQuery() {
        Class<?> expression = getExpression();
        Cypher cypher = expression.getAnnotation(Cypher.class);
        if (cypher == null) {
            throw new CdoException("Type '" + expression.getName() + "' is not annotated with '" + Cypher.class.getName() + "'");
        }
        return cypher.value();
    }
}
