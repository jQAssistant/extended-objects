package com.buschmais.xo.neo4j.impl.datastore;

import java.util.Iterator;
import java.util.Map;

import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.util.QueryResult;

import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;

public class RestCypherQueryEngine implements NativeQueryEngine<CypherQuery> {

    final org.neo4j.rest.graphdb.query.RestCypherQueryEngine exutionEngine;

    public RestCypherQueryEngine(final RestGraphDatabase graphDatabaseService) {
        final RestAPI restAPI = graphDatabaseService.getRestAPI();
        exutionEngine = new org.neo4j.rest.graphdb.query.RestCypherQueryEngine(restAPI);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(final CypherQuery query, final Map<String, Object> parameters) {
        final QueryResult<Map<String, Object>> queryResult = exutionEngine.query(query.getExpression(), parameters);
        final Iterator<Map<String, Object>> iterator = queryResult.iterator();
        return new ResultIterator<Map<String, Object>>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public void close() {
            }
        };

    }

}
