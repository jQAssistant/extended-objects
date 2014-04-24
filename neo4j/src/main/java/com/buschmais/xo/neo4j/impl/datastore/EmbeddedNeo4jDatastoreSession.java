package com.buschmais.xo.neo4j.impl.datastore;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.api.NativeQueryEngine;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Lucene;
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
    // private final ExecutionEngine executionEngine;

    private final NativeQueryEngine<CypherQuery> cypherQueryEngine;
    private final NativeQueryEngine<LuceneQuery> luceneQueryEngine;

    public EmbeddedNeo4jDatastoreSession(final GraphDatabaseService graphDatabaseService) {
        super(graphDatabaseService);
        datastoreTransaction = new EmbeddedNeo4jDatastoreTransaction();
        // executionEngine = new ExecutionEngine(graphDatabaseService);

        // TODO: dynamically register native query engines - plugins?
        cypherQueryEngine = new EmbeddedCypherQueryEngine(graphDatabaseService);
        luceneQueryEngine = new EmbeddedLuceneQueryEngine(graphDatabaseService);
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return datastoreTransaction;
    }

    @Override
    public <QL> ResultIterator<Map<String, Object>> executeQuery(final QL expression, final Map<String, Object> parameters) {
        final NativeQuery<?> query = getNativeQuery(expression);
        final Map<String, Object> effectiveParameters = translateParameters(parameters);
        // TODO: dynamically select query engine (map lookup?)
        if (query instanceof CypherQuery) {
            return cypherQueryEngine.execute((CypherQuery)query, effectiveParameters);
        } else {
            return luceneQueryEngine.execute((LuceneQuery)query, effectiveParameters);
        }

        // final ExecutionResult executionResult =
        // executionEngine.execute(getCypher(expression),
        // translateParameters(parameters));
        // return new ResourceResultIterator(executionResult.iterator());
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
