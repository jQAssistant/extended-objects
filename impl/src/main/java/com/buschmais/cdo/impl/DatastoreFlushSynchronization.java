package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class DatastoreFlushSynchronization implements CdoTransaction.Synchronization {

    private TransactionalCache transactionalCache;

    private DatastoreSession<?, ?, ?, ?, ?> datastoreSession;

    public DatastoreFlushSynchronization(TransactionalCache<?> cache, DatastoreSession datastoreSession) {
        this.transactionalCache = cache;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public void beforeCompletion() {
        datastoreSession.flush(transactionalCache.values());
    }

    @Override
    public void afterCompletion(boolean committed) {
    }
}
