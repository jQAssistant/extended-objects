package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.api.CdoTransaction;

public class CacheSynchronization implements CdoTransaction.Synchronization{

    private TransactionalCache<?> transactionalCache;

    public CacheSynchronization(TransactionalCache<?> transactionalCache) {
        this.transactionalCache = transactionalCache;
    }

    @Override
    public void beforeCompletion() {
    }

    @Override
    public void afterCompletion(boolean committed) {
        transactionalCache.afterCompletion(committed);
    }
}
