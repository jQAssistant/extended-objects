package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.spi.datastore.DatastoreTransaction;

import java.util.*;

public class CdoTransactionImpl implements CdoTransaction {

    private final DatastoreTransaction datastoreTransaction;

    private final List<Synchronization> defaultSynchronizations = new ArrayList<>();
    private final List<Synchronization> synchronizations = new ArrayList<>();

    public CdoTransactionImpl(DatastoreTransaction datastoreTransaction) {
        this.datastoreTransaction = datastoreTransaction;
    }

    @Override
    public void begin() {
        datastoreTransaction.begin();
    }

    @Override
    public void commit() {
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
        }
    }

    @Override
    public boolean isActive() {
        return datastoreTransaction.isActive();
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

    public void unregisterDefaultSynchronization(Synchronization synchronization) {
        defaultSynchronizations.remove(synchronization);
    }

    private void beforeCompletion() {
        executeSynchronizations(new SynchronizationOperation() {
            @Override
            public void run(Synchronization synchronization) {
                synchronization.beforeCompletion();
            }
        });
    }

    private void afterCompletion(final boolean committed) {
        executeSynchronizations(new SynchronizationOperation() {
            @Override
            public void run(Synchronization synchronization) {
                synchronization.afterCompletion(committed);
            }
        });
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

    private interface SynchronizationOperation {
        void run(Synchronization synchronization);
    }
}
