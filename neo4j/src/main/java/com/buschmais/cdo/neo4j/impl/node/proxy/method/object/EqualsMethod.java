package com.buschmais.cdo.neo4j.impl.node.proxy.method.object;

import com.buschmais.cdo.neo4j.impl.common.InstanceManager;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import com.buschmais.cdo.spi.proxy.ProxyMethod;

public class EqualsMethod<Entity> implements ProxyMethod<Entity> {

    private InstanceManager<?, Entity> instanceManager;

    private DatastoreSession<?, Entity, ?, ?, ?, ?, ?> datastoreSession;

    public EqualsMethod(InstanceManager<?, Entity> instanceManager, DatastoreSession<?, Entity, ?, ?, ?, ?, ?> datastoreSession) {
        this.instanceManager = instanceManager;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object other = args[0];
        if (instanceManager.isEntity(other)) {
            Entity otherEntity = instanceManager.getEntity(other);
            return (datastoreSession.getId(otherEntity).equals(datastoreSession.getId(entity)));
        }
        return Boolean.valueOf(false);
    }
}
