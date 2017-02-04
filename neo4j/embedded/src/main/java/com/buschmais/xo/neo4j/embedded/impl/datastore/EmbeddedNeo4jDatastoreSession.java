package com.buschmais.xo.neo4j.embedded.impl.datastore;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

public class EmbeddedNeo4jDatastoreSession extends AbstractNeo4jDatastoreSession<GraphDatabaseService> {

    private class EmbeddedNeo4jDatastoreTransaction implements DatastoreTransaction {

        private Transaction transaction;

        @Override
        public void begin() {
            if (transaction != null) {
                throw new XOException("There is already an existing transaction.");
            }
            transaction = getGraphDatabaseService().beginTx();
        }

        @Override
        public void commit() {
            ensureTransaction();
            transaction.success();
            closeTransaction();
        }

        @Override
        public void rollback() {
            ensureTransaction();
            transaction.failure();
            closeTransaction();
        }

        @Override
        public boolean isActive() {
            return transaction != null;
        }

        private void ensureTransaction() {
            if (transaction == null) {
                throw new XOException("There is no existing transaction.");
            }
        }

        private void closeTransaction() {
            transaction.close();
            transaction = null;
        }
    }

    private final DatastoreTransaction datastoreTransaction;

    public EmbeddedNeo4jDatastoreSession(GraphDatabaseService graphDatabaseService) {
        super(graphDatabaseService);
        datastoreTransaction = new EmbeddedNeo4jDatastoreTransaction();
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return datastoreTransaction;
    }

    @Override
    public <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage) {
        if (Cypher.class.equals(queryLanguage)) {
            return (DatastoreQuery<QL>) new EmbeddedNeo4jCypherQuery();
        }
        throw new XOException("Unsupported query language: " + queryLanguage.getName());
    }

    public class EmbeddedNeo4jCypherQuery implements DatastoreQuery<Cypher> {

        @Override
        public ResultIterator<Map<String, Object>> execute(Cypher expression, Map<String, Object> parameters) {
            return execute(expression.value(), parameters);
        }

        @Override
        public ResultIterator<Map<String, Object>> execute(String expression, Map<String, Object> parameters) {
            Result executionResult = getGraphDatabaseService().execute(expression, convertParameters(parameters));
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
                        result.put(column, convertValue(next.get(column)));
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

        private Map<String, Object> convertParameters(Map<String, Object> parameters) {
            Map<String, Object> effectiveParameters = new HashMap<>();
            for (Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
                Object value = parameterEntry.getValue();
                value = convertParameter(value);
                effectiveParameters.put(parameterEntry.getKey(), value);
            }
            return effectiveParameters;
        }
    }
}
