package com.buschmais.xo.neo4j.impl.datastore;

import java.util.Map;

import org.neo4j.rest.graphdb.RestGraphDatabase;

import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;

public class RestLuceneQueryEngine implements NativeQueryEngine<LuceneQuery> {

    public RestLuceneQueryEngine(final RestGraphDatabase graphDatabaseService) {
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(final LuceneQuery query, final Map<String, Object> translateParameters) {
        // http://docs.neo4j.org/chunked/stable/rest-api-indexes.html
        return null;
    }

}
