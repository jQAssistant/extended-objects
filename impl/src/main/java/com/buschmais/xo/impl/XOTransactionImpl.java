package com.buschmais.xo.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOTransaction;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;

public class XOTransactionImpl implements XOTransaction {

    private final DatastoreTransaction datastoreTransaction;

    private final Collection<Synchronization> defaultSynchronizations = new LinkedList<>();
    private final Collection<Synchronization> synchronizations = new LinkedList<>();

    private boolean rollbackOnly;

    public XOTransactionImpl(DatastoreTransaction datastoreTransaction) {
        this.datastoreTransaction = datastoreTransaction;
    }

    @Override
    public XOTransaction begin() {
        datastoreTransaction.begin();
        return this;
    }

    @Override
    public void commit() {
        if (rollbackOnly) {
            throw new XOException("Transaction is marked as rollback only.");
        }
        beforeCompletion();
        boolean committed = false;
        try {
            datastoreTransaction.commit();
            committed = true;
        } finally {
            afterCompletion(committed);
        }
    }

    @Override
    public void rollback() {
        try {
            datastoreTransaction.rollback();
        } finally {
            afterCompletion(false);
            rollbackOnly = false;
        }
    }

    @Override
    public boolean isActive() {
        return datastoreTransaction.isActive();
    }

    @Override
    public void setRollbackOnly() {
        rollbackOnly = true;
    }

    @Override
    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) {
        synchronizations.add(synchronization);
    }

    @Override
    public void unregisterSynchronization(Synchronization synchronization) {
        synchronizations.remove(synchronization);
    }

    public void registerDefaultSynchronization(Synchronization synchronization) {
        defaultSynchronizations.add(synchronization);
    }

    private void beforeCompletion() {
        executeSynchronizations(synchronization -> synchronization.beforeCompletion());
    }

    private void afterCompletion(final boolean committed) {
        executeSynchronizations(synchronization -> synchronization.afterCompletion(committed));
        synchronizations.clear();
    }

    private void executeSynchronizations(SynchronizationOperation operation) {
        for (Synchronization synchronization : defaultSynchronizations) {
            operation.run(synchronization);
        }
        for (Synchronization synchronization : new ArrayList<>(synchronizations)) {
            operation.run(synchronization);
        }
    }

    @Override
    public void close() {
        if (rollbackOnly) {
            rollback();
        } else {
            commit();
        }
    }

    private interface SynchronizationOperation {
        void run(Synchronization synchronization);
    }
}
