package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.api.CdoTransaction;

public class CacheSynchronization<Entity, Relation> implements CdoTransaction.Synchronization {

    private final CacheSynchronizationService<Entity, Relation> cacheSynchronizationService;
    private final TransactionalCache<?>[] caches;

    public CacheSynchronization(CacheSynchronizationService<Entity, Relation> cacheSynchronizationService, TransactionalCache<?>... caches) {
        this.cacheSynchronizationService = cacheSynchronizationService;
        this.caches = caches;
    }

    @Override
    public void beforeCompletion() {
        cacheSynchronizationService.flush();
    }

    @Override
    public void afterCompletion(boolean committed) {
        for (TransactionalCache<?> cache : caches) {
            cache.afterCompletion(committed);
        }
    }
}
