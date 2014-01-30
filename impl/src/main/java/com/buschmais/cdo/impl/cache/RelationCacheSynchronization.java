package com.buschmais.cdo.impl.cache;

import com.buschmais.cdo.impl.AbstractInstanceManager;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class RelationCacheSynchronization<Relation> extends AbstractCacheSynchronization<Relation> {

    private final DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession;

    public RelationCacheSynchronization(AbstractInstanceManager<?, Relation> instanceManager, TransactionalCache<?> cache, DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession) {
        super(instanceManager, cache);
        this.datastoreSession = datastoreSession;
    }

    @Override
    protected void flush(Relation relation) {
        datastoreSession.flushRelation(relation);
    }
}
