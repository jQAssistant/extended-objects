package com.buschmais.xo.neo4j.impl.datastore;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.neo4j.api.annotation.Lucene;

public class LuceneQuery implements NativeQuery<Lucene> {

    private final String expression;
    private final Class<?> meta;

    public LuceneQuery(final String expression, final Class<?> meta) {
        this.expression = expression;
        this.meta = meta;
    }

    @Override
    public String getExpression() {
        return expression;
    }

}
