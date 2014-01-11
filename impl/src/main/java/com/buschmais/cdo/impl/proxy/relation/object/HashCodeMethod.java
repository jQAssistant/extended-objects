package com.buschmais.cdo.impl.proxy.relation.object;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class HashCodeMethod<Relation> implements ProxyMethod<Relation> {

    private DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession;

    public HashCodeMethod(DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession) {
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Relation relation, Object instance, Object[] args) {
        return datastoreSession.getRelationId(relation).hashCode();
    }
}
