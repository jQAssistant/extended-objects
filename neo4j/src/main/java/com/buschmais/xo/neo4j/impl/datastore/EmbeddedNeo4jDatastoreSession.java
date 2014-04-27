package com.buschmais.xo.neo4j.impl.datastore;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.impl.datastore.query.EmbeddedCypherQueryEngine;
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
            transaction.success();
            closeTransaction();
        }

        @Override
        public void rollback() {
            transaction.failure();
            closeTransaction();
        }

        @Override
        public boolean isActive() {
            return transaction != null;
        }

        private void closeTransaction() {
            transaction.close();
            transaction = null;
        }
    }

    private final DatastoreTransaction datastoreTransaction;

    private final NativeQueryEngine<?> cypherQueryEngine;

    public EmbeddedNeo4jDatastoreSession(final GraphDatabaseService graphDatabaseService) {
        super(graphDatabaseService);
        datastoreTransaction = new EmbeddedNeo4jDatastoreTransaction();

        // TODO: dynamically register native query engines - plugins?
        cypherQueryEngine = new EmbeddedCypherQueryEngine(graphDatabaseService);
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return datastoreTransaction;
    }

    @Override
    public ResultIterator<Map<String, Object>> executeQuery(final NativeQuery query, final Map<String, Object> parameters) {
        final Map<String, Object> effectiveParameters = translateParameters(parameters);
        final NativeQueryEngine engine = getNativeQueryEngine(query);
        return engine.execute(query, effectiveParameters);
    }

    @Override
    public NativeQueryEngine getNativeQueryEngine(final NativeQuery<?> query) {
        return cypherQueryEngine;
    }

    private Map<String, Object> translateParameters(final Map<String, Object> parameters) {
        final Map<String, Object> effectiveParameters = new HashMap<>();
        for (final Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
            Object value = parameterEntry.getValue();
            if (value instanceof Node) {
                value = ((Node) value).getId();
            } else if (value instanceof Relationship) {
                value = ((Relationship) value).getId();
            }
            effectiveParameters.put(parameterEntry.getKey(), value);
        }
        return effectiveParameters;
    }
}
