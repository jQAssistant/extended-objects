package com.buschmais.xo.neo4j.impl.datastore;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.entity.RestEntity;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

public class RestNeo4jDatastoreSession extends AbstractNeo4jDatastoreSession<RestGraphDatabase> {

    private class RestNeo4jDatastoreTransaction implements DatastoreTransaction {

        @Override
        public void begin() {
        }

        @Override
        public void commit() {
        }

        @Override
        public void rollback() {
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }

    private final DatastoreTransaction transaction;

    public RestNeo4jDatastoreSession(RestGraphDatabase graphDatabaseService) {
        super(graphDatabaseService);
        transaction = new RestNeo4jDatastoreTransaction();
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return transaction;
    }

    @Override
    public <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage) {
        if (Cypher.class.equals(queryLanguage)) {
            return (DatastoreQuery<QL>) new RestNeo4jCypherQuery();
        }
        throw new XOException("Unsupported query language: " + queryLanguage.getName());
    }

    public class RestNeo4jCypherQuery implements DatastoreQuery<Cypher> {

        @Override
        public ResultIterator<Map<String, Object>> execute(Cypher expression, Map<String, Object> parameters) {
            return execute(expression.value(), parameters);
        }

        @Override
        public ResultIterator<Map<String, Object>> execute(String expression, Map<String, Object> parameters) {
            Map<String, Object> effectiveParameters = translateParameters(parameters);
            RestAPI restAPI = getGraphDatabaseService().getRestAPI();
            RestCypherQueryEngine restCypherQueryEngine = new RestCypherQueryEngine(restAPI);
            QueryResult<Map<String, Object>> queryResult = restCypherQueryEngine.query(getCypher(expression), effectiveParameters);

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

        private Map<String, Object> translateParameters(Map<String, Object> parameters) {
            Map<String, Object> effectiveParameters = new HashMap<>();
            for (Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
                Object value = parameterEntry.getValue();
                if (value instanceof RestEntity) {
                    value = ((RestEntity) value).getId();
                }
                effectiveParameters.put(parameterEntry.getKey(), value);
            }
            return effectiveParameters;
        }
    }
}
