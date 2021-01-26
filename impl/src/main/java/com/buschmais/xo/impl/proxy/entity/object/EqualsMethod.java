package com.buschmais.xo.impl.proxy.entity.object;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.api.metadata.type.DatastoreEntityMetadata;
import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.session.InstanceManager;

public class EqualsMethod<Entity> implements ProxyMethod<Entity> {

    private final SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext;

    public EqualsMethod(SessionContext<?, Entity, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object other = args[0];
        InstanceManager<?, Entity> entityInstanceManager = sessionContext.getEntityInstanceManager();
        if (entityInstanceManager.isInstance(other)) {
            Entity otherEntity = entityInstanceManager.getDatastoreType(other);
            DatastoreSession<?, Entity, ? extends DatastoreEntityMetadata<?>, ?, ?, ?, ? extends DatastoreRelationMetadata<?>, ?, ?> datastoreSession = sessionContext
                    .getDatastoreSession();
            return datastoreSession.getDatastoreEntityManager().getEntityId(otherEntity)
                    .equals(datastoreSession.getDatastoreEntityManager().getEntityId(entity));
        }
        return Boolean.FALSE;
    }
}
