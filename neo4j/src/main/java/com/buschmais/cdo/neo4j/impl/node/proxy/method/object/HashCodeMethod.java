package com.buschmais.cdo.neo4j.impl.node.proxy.method.object;

import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.spi.proxy.ProxyMethod;

public class HashCodeMethod<Entity> implements ProxyMethod<Entity> {

    private DatastoreSession<?, Entity, ?, ?, ?, ?, ?> datastoreSession;

    public HashCodeMethod(DatastoreSession<?, Entity, ?, ?, ?, ?, ?> datastoreSession) {
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return datastoreSession.getId(entity).hashCode();
    }
}
