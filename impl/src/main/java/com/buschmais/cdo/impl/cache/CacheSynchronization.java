package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

import java.util.Collection;

public class CacheSynchronization<Entity> implements CdoTransaction.Synchronization {

    private InstanceManager<?, Entity, ?, ?, ?, ?> instanceManager;

    private TransactionalCache<?> transactionalCache;

    private DatastoreSession<?, Entity, ?, ?, ?, ?, ?, ?> datastoreSession;

    public CacheSynchronization(InstanceManager<?, Entity, ?, ? ,? ,?> instanceManager, TransactionalCache<?> transactionalCache, DatastoreSession<?, Entity, ?, ?, ?, ?, ? ,?> datastoreSession) {
        this.instanceManager = instanceManager;
        this.transactionalCache = transactionalCache;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public void beforeCompletion() {
        Collection instances = transactionalCache.values();
        for (Object instance : instances) {
            Entity entity = instanceManager.getEntity(instance);
            datastoreSession.flush(entity);
        }
    }

    @Override
    public void afterCompletion(boolean committed) {
        transactionalCache.afterCompletion(committed);
    }
}
