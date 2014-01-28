package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.impl.AbstractInstanceManager;

public abstract class CacheSynchronization<DatastoreType> implements CdoTransaction.Synchronization {

    private AbstractInstanceManager<?, DatastoreType> instanceManager;

    private TransactionalCache<?> cache;

    public CacheSynchronization(AbstractInstanceManager<?, DatastoreType> instanceManager,TransactionalCache<?> cache) {
        this.instanceManager = instanceManager;
        this.cache = cache;
    }

    @Override
    public void beforeCompletion() {
        for (Object instance : cache.values()) {
            DatastoreType datastoreType = instanceManager.getDatastoreType(instance);
            flush(datastoreType);
        }
    }

    protected abstract void flush(DatastoreType datastoreType);

    @Override
    public void afterCompletion(boolean committed) {
        cache.afterCompletion(committed);
    }
}
