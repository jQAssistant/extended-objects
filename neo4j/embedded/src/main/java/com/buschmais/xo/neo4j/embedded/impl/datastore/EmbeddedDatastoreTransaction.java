package com.buschmais.xo.neo4j.embedded.impl.datastore;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

public class EmbeddedDatastoreTransaction implements DatastoreTransaction {

    private GraphDatabaseService graphDatabaseService;
    private Transaction transaction;

    public EmbeddedDatastoreTransaction(GraphDatabaseService graphDatabaseService) {
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
        transaction.commit();
        closeTransaction();
    }

    @Override
    public void rollback() {
        ensureTransaction();
        transaction.rollback();
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
