package com.buschmais.cdo.impl.proxy.relation.object;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class EqualsMethod<Relation> implements ProxyMethod<Relation> {

    private SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext;


    public EqualsMethod(SessionContext<?, ?, ?, ?, ?, Relation, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Relation relation, Object instance, Object[] args) {
        Object other = args[0];
        InstanceManager<?, Relation> instanceManager = sessionContext.getRelationInstanceManager();
        if (instanceManager.isInstance(other)) {
            Relation otherRelation = instanceManager.getDatastoreType(other);
            DatastoreSession<?, ?, ? extends DatastoreEntityMetadata<?>, ?, ?, Relation, ? extends DatastoreRelationMetadata<?>, ?> datastoreSession = sessionContext.getDatastoreSession();
            return (datastoreSession.getRelationId(otherRelation).equals(datastoreSession.getRelationId(relation)));
        }
        return Boolean.FALSE;
    }
}
