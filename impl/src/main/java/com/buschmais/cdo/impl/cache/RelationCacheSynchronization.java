package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

/**
 * Created by Dirk Mahler on 14.01.14.
 */
public class RelationCacheSynchronization<Relation> extends CacheSynchronization<Relation> {

    private DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession;

    public RelationCacheSynchronization(AbstractInstanceManager<?, Relation> instanceManager, TransactionalCache<?> cache, DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession) {
        super(instanceManager, cache);
        this.datastoreSession = datastoreSession;
    }

    @Override
    protected void flush(Relation relation) {
        datastoreSession.flushRelation(relation);
    }
}
