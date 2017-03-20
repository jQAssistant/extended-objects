package com.buschmais.xo.neo4j.remote.impl.datastore;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementRunner;
import org.neo4j.driver.v1.Transaction;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

public class RemoteDatastoreTransaction implements DatastoreTransaction {

    private Session session;

    private Transaction transaction = null;

    public RemoteDatastoreTransaction(Session session) {
        this.session = session;
    }

    @Override
    public void begin() {
        if (transaction != null) {
            throw new XOException("There is already an existing transaction.");
        }
        this.transaction = session.beginTransaction();
    }

    @Override
    public void commit() {
        assertTransaction();
        try {
            this.transaction.success();
        } finally {
            close();
        }
    }

    @Override
    public void rollback() {
        assertTransaction();
        try {
            this.transaction.failure();
        } finally {
            close();
        }
    }

    private void close() {
        assertTransaction();
        this.transaction.close();
        this.transaction = null;
    }

    private void assertTransaction() {
        if (transaction == null) {
            throw new XOException("There is no existing transaction.");
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
