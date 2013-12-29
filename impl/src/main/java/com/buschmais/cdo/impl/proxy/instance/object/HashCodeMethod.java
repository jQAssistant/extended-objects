package com.buschmais.cdo.impl.proxy.instance.object;

import com.buschmais.cdo.spi.datastore.DatastoreSession;
import com.buschmais.cdo.api.proxy.ProxyMethod;

public class HashCodeMethod<Entity> implements ProxyMethod<Entity> {

    private final DatastoreSession<?, Entity, ?, ?, ?, ?> datastoreSession;

    public HashCodeMethod(DatastoreSession<?, Entity,?, ?, ?, ?> datastoreSession) {
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return datastoreSession.getId(entity).hashCode();
    }
}
