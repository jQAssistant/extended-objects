package com.buschmais.xo.neo4j.remote.impl;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementRunner;
import org.neo4j.driver.v1.Transaction;

import com.buschmais.xo.spi.datastore.DatastoreTransaction;

public class RemoteDatastoreTransaction implements DatastoreTransaction {

    private Session session;

    private Transaction transaction = null;

    public RemoteDatastoreTransaction(Session session) {
        this.session = session;
    }

    @Override
    public void begin() {
        this.transaction = session.beginTransaction();
    }

    @Override
    public void commit() {
        try {
            this.transaction.success();
        } finally {
            this.transaction.close();
        }
    }

    @Override
    public void rollback() {
        try {
            this.transaction.failure();
        } finally {
            this.transaction.close();
        }
    }

    @Override
    public boolean isActive() {
        return transaction != null && transaction.isOpen();
    }

    public StatementRunner getStatementRunner() {
        return transaction != null ? transaction : session;
    }
}
