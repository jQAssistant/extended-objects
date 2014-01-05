package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class CacheSynchronization<Entity, Relation> implements CdoTransaction.Synchronization {

    private InstanceManager<?, Entity, ?, ?, Relation, ?> instanceManager;

    private TransactionalCache<?> entityCache;

    private TransactionalCache<?> relationCache;

    private DatastoreSession<?, Entity, ?, ?, ?, Relation, ?, ?> datastoreSession;

    public CacheSynchronization(InstanceManager<?, Entity, ?, ?, Relation, ?> instanceManager, TransactionalCache<?> entityCache, TransactionalCache<?> relationCache, DatastoreSession<?, Entity, ?, ?, ?, Relation, ?, ?> datastoreSession) {
        this.instanceManager = instanceManager;
        this.entityCache = entityCache;
        this.relationCache = relationCache;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public void beforeCompletion() {
        for (Object instance : relationCache.values()) {
            Relation relation = instanceManager.getRelation(instance);
            datastoreSession.flushRelation(relation);
        }
        for (Object instance : entityCache.values()) {
            Entity entity = instanceManager.getEntity(instance);
            datastoreSession.flushEntity(entity);
        }
    }

    @Override
    public void afterCompletion(boolean committed) {
        entityCache.afterCompletion(committed);
    }
}
