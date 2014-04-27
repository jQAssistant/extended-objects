package com.buschmais.xo.neo4j.api.query;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.neo4j.api.annotation.Cypher;

public class CypherQuery implements NativeQuery<Cypher> {

    private final String expression;

    public CypherQuery(final String expression) {
        this.expression = expression;
    }

    @Override
    public String getExpression() {
        return expression;
    }

}
