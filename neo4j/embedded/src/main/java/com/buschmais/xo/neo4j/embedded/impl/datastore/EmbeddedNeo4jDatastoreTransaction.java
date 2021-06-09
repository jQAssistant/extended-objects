package com.buschmais.xo.neo4j.embedded.impl.datastore;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

class EmbeddedNeo4jDatastoreTransaction implements DatastoreTransaction {

    private GraphDatabaseService graphDatabaseService;
    private Transaction transaction;

    public EmbeddedNeo4jDatastoreTransaction(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public void begin() {
        if (transaction != null) {
            throw new XOException("There is already an existing transaction.");
        }
        transaction = graphDatabaseService.beginTx();
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

    public Transaction getTransaction() {
        ensureTransaction();
        return transaction;
    }

    private void ensureTransaction() {
        if (transaction == null) {
            throw new XOException("There is no active transaction.");
        }
    }

    private void closeTransaction() {
        transaction.close();
        transaction = null;
    }
}
