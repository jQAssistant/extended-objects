package com.buschmais.xo.impl;

import java.util.Set;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.proxy.ProxyMethodService;
import com.buschmais.xo.impl.proxy.entity.EntityProxyMethodService;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;

public class EntityInstanceManager<EntityId, Entity, EntityDiscriminator> extends AbstractInstanceManager<EntityId, Entity> {

    private final SessionContext<EntityId, Entity, ?, EntityDiscriminator, ?, ?, ?, ?, ?> sessionContext;
    private final ProxyMethodService<Entity> proxyMethodService;

    public EntityInstanceManager(SessionContext<EntityId, Entity, ?, EntityDiscriminator, ?, ?, ?, ?, ?> sessionContext) {
        super(sessionContext.getEntityCache(), sessionContext.getInstanceListenerService(), sessionContext.getProxyFactory());
        this.sessionContext = sessionContext;
        this.proxyMethodService = new EntityProxyMethodService<>(sessionContext);
    }

    @Override
    public boolean isDatastoreType(Object o) {
        return sessionContext.getDatastoreSession().getDatastoreEntityManager().isEntity(o);
    }

    @Override
    public EntityId getDatastoreId(Entity entity) {
        if (entity == null) {
            throw new XOException("No entity provided.");
        }
        return sessionContext.getDatastoreSession().getDatastoreEntityManager().getEntityId(entity);
    }

    @Override
    protected TypeMetadataSet<?> getTypes(Entity entity) {
        Set<EntityDiscriminator> discriminators = sessionContext.getDatastoreSession().getDatastoreEntityManager().getEntityDiscriminators(entity);
        return sessionContext.getMetadataProvider().getTypes(discriminators);
    }

    @Override
    protected ProxyMethodService<Entity> getProxyMethodService() {
        return proxyMethodService;
    }
}
