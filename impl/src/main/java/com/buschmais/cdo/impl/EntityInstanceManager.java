package com.buschmais.cdo.impl;

import com.buschmais.cdo.impl.proxy.ProxyMethodService;
import com.buschmais.cdo.impl.proxy.entity.EntityProxyMethodService;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;

import java.util.Set;

public class EntityInstanceManager<EntityId, Entity, EntityDiscriminator> extends InstanceManager<EntityId, Entity> {

    private SessionContext<EntityId, Entity, ?, EntityDiscriminator, ?, ?, ?, ?> sessionContext;
    private ProxyMethodService<Entity, ?> proxyMethodService;

    public EntityInstanceManager(SessionContext<EntityId, Entity, ?, EntityDiscriminator, ?, ?, ?, ?> sessionContext) {
        super(sessionContext.getEntityCache(), sessionContext.getProxyFactory());
        this.sessionContext = sessionContext;
        this.proxyMethodService = new EntityProxyMethodService<>(sessionContext);
    }

    @Override
    public boolean isDatastoreType(Object o) {
        return sessionContext.getDatastoreSession().isEntity(o);
    }

    @Override
    protected EntityId getDatastoreId(Entity entity) {
        return sessionContext.getDatastoreSession().getId(entity);
    }

    @Override
    protected TypeMetadataSet<?> getTypes(Entity entity) {
        Set<EntityDiscriminator> discriminators = sessionContext.getDatastoreSession().getEntityDiscriminators(entity);
        TypeMetadataSet<?> types = sessionContext.getMetadataProvider().getTypes(discriminators);
        return types;
    }

    @Override
    protected ProxyMethodService<Entity, ?> getProxyMethodService() {
        return proxyMethodService;
    }
}
