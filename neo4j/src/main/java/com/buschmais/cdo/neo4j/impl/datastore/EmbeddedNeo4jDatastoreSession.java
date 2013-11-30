package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.ResultIterator;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.util.Map;

public class EmbeddedNeo4jDatastoreSession extends AbstractNeo4jDatastoreSession<GraphDatabaseService> {

    private Transaction transaction;
    private final ExecutionEngine executionEngine;

    public EmbeddedNeo4jDatastoreSession(GraphDatabaseService graphDatabaseService, NodeMetadataProvider metadataProvider) {
        super(graphDatabaseService, metadataProvider);
        executionEngine = new ExecutionEngine(graphDatabaseService);
    }

    public ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
        ExecutionResult executionResult = executionEngine.execute(query, parameters);
        return new Neo4jResultIterator(executionResult.iterator());
    }

    @Override
    public void begin() {
        if (transaction != null) {
            throw new CdoException("There is already an existing transaction.");
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

    private void closeTransaction() {
        transaction.close();
        transaction = null;
    }

}
