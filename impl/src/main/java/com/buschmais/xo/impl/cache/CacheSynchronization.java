package com.buschmais.xo.impl.cache;

import com.buschmais.xo.api.XOTransaction;

public class CacheSynchronization<Entity, Relation> implements XOTransaction.Synchronization {

    private final CacheSynchronizationService<Entity, Relation> cacheSynchronizationService;

    public CacheSynchronization(CacheSynchronizationService<Entity, Relation> cacheSynchronizationService) {
        this.cacheSynchronizationService = cacheSynchronizationService;
    }

    @Override
    public void beforeCompletion() {
        cacheSynchronizationService.flush();
    }

    @Override
    public void afterCompletion(boolean committed) {
        cacheSynchronizationService.clear();
    }
}
