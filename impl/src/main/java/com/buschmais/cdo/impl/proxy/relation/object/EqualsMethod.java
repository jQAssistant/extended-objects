package com.buschmais.cdo.impl.proxy.relation.object;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class EqualsMethod<Relation> implements ProxyMethod<Relation> {

    private InstanceManager<?, ?, ?, ?, Relation, ?> instanceManager;

    private DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession;

    public EqualsMethod(InstanceManager<?, ?, ?, ?, Relation, ?> instanceManager, DatastoreSession<?, ?, ?, ?, ?, Relation, ?, ?> datastoreSession) {
        this.instanceManager = instanceManager;
        this.datastoreSession = datastoreSession;
    }

    @Override
    public Object invoke(Relation relation, Object instance, Object[] args) {
        Object other = args[0];
        if (instanceManager.isRelation(other)) {
            Relation otherRelation = instanceManager.getRelation(other);
            return (datastoreSession.getRelationId(otherRelation).equals(datastoreSession.getRelationId(relation)));
        }
        return Boolean.valueOf(false);
    }
}
