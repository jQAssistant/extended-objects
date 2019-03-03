package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.spi.datastore.DatastoreQuery;

import org.neo4j.graphdb.Result;

public class EmbeddedNeo4jCypherQuery implements DatastoreQuery<Cypher> {

    private EmbeddedNeo4jDatastoreSession embeddedNeo4jDatastoreSession;

    public EmbeddedNeo4jCypherQuery(EmbeddedNeo4jDatastoreSession embeddedNeo4jDatastoreSession) {
        this.embeddedNeo4jDatastoreSession = embeddedNeo4jDatastoreSession;
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(Cypher expression, Map<String, Object> parameters) {
        return execute(expression.value(), parameters);
    }

    @Override
    public ResultIterator<Map<String, Object>> execute(String expression, Map<String, Object> parameters) {
        Map<String, Object> convertedParameters = (Map<String, Object>) embeddedNeo4jDatastoreSession.convertParameter(parameters);
        Result executionResult = embeddedNeo4jDatastoreSession.getGraphDatabaseService().execute(expression, convertedParameters);
        final List<String> columns = executionResult.columns();
        return new ResultIterator<Map<String, Object>>() {

            @Override
            public boolean hasNext() {
                return executionResult.hasNext();
            }

            @Override
            public Map<String, Object> next() {
                Map<String, Object> next = executionResult.next();
                Map<String, Object> result = new LinkedHashMap<>(next.size(), 1);
                for (String column : columns) {
                    result.put(column, embeddedNeo4jDatastoreSession.convertValue(next.get(column)));
                }
                return result;
            }

            @Override
            public void remove() {
                throw new XOException("Remove operation is not supported for query results.");
            }

            @Override
            public void close() {
                executionResult.close();
            }
        };
    }
}
