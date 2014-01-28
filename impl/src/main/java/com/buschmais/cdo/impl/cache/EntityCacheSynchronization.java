package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

/**
 * Created by Dirk Mahler on 14.01.14.
 */
public class EntityCacheSynchronization<Entity> extends CacheSynchronization<Entity> {

    private DatastoreSession<?, Entity, ?, ?, ?, ?, ?, ?> datastoreSession;

    public EntityCacheSynchronization(AbstractInstanceManager<?, Entity> instanceManager, TransactionalCache<?> cache, DatastoreSession<?, Entity, ?, ?, ?, ?, ?, ?> datastoreSession) {
        super(instanceManager, cache);
        this.datastoreSession = datastoreSession;
    }

    @Override
    protected void flush(Entity entity) {
        datastoreSession.flushEntity(entity);
    }
}
