package com.buschmais.cdo.impl.proxy.entity.object;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

public class EqualsMethod<Entity> implements ProxyMethod<Entity> {

    private SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext;

    public EqualsMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object other = args[0];
        InstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        if (entityInstanceManager.isInstance(other)) {
            Entity otherEntity = entityInstanceManager.getDatastoreType(other);
            DatastoreSession<?, Entity, ? extends DatastoreEntityMetadata<?>, ?, ?, ?, ? extends DatastoreRelationMetadata<?>, ?> datastoreSession = sessionContext.getDatastoreSession();
            return (datastoreSession.getId(otherEntity).equals(datastoreSession.getId(entity)));
        }
        return Boolean.FALSE;
    }
}
