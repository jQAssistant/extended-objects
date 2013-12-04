package com.buschmais.cdo.neo4j.impl.common;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.neo4j.impl.cache.TransactionalCache;

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
