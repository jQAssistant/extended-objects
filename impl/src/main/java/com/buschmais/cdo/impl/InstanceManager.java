package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.proxy.ProxyMethodService;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.proxy.relation.RelationProxyMethodService;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;
import com.buschmais.cdo.impl.proxy.entity.InstanceInvocationHandler;
import com.buschmais.cdo.impl.proxy.entity.EntityProxyMethodService;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

public class InstanceManager<EntityId, Entity, EntityDiscriminator, RelationId, Relation, RelationDiscriminator> {

    private final MetadataProvider<?, EntityDiscriminator, ?, RelationDiscriminator> metadataProvider;
    private final DatastoreSession<EntityId, Entity, ?, EntityDiscriminator, RelationId, Relation, ?, RelationDiscriminator> datastoreSession;
    private final TransactionalCache<EntityId> entityCache;
    private final TransactionalCache<RelationId> relationCache;
    private final ProxyMethodService<Entity, ?> entityProxyMethodService;
    private final ProxyMethodService<Relation, ?> relationProxyMethodService;
    private final ProxyFactory proxyFactory;

    public InstanceManager(MetadataProvider metadataProvider, PropertyManager<EntityId, Entity, RelationId, Relation> propertyManager, DatastoreSession<EntityId, Entity, ?, EntityDiscriminator, RelationId, Relation, ?, RelationDiscriminator> datastoreSession, CdoTransaction cdoTransaction, TransactionalCache<EntityId> entityCache, TransactionalCache relationCache, ProxyFactory proxyFactory, InterceptorFactory interceptorFactory) {
        this.metadataProvider = metadataProvider;
        this.datastoreSession = datastoreSession;
        this.entityCache = entityCache;
        this.relationCache = relationCache;
        this.proxyFactory = proxyFactory;
        this.entityProxyMethodService = new EntityProxyMethodService(metadataProvider, this, proxyFactory, propertyManager, cdoTransaction, interceptorFactory, datastoreSession);
        this.relationProxyMethodService = new RelationProxyMethodService<>(metadataProvider, this, proxyFactory, propertyManager, cdoTransaction, interceptorFactory, datastoreSession);
    }

    public <T> T getRelationInstance(Relation relation) {
        Entity source = datastoreSession.getDatastorePropertyManager().getSource(relation);
        Entity target = datastoreSession.getDatastorePropertyManager().getTarget(relation);
        RelationDiscriminator discriminator = datastoreSession.getRelationDiscriminator(relation);
        if (discriminator == null) {
            throw new CdoException("Cannot determine type discriminators for relation '" + relation + "'");
        }
        TypeMetadataSet<?> types = metadataProvider.getRelationTypes(getEntityDiscriminators(source), discriminator, getEntityDiscriminators(target));
        RelationId id = datastoreSession.getRelationId(relation);
        return (T) getOrCreateInstance(relationCache, id, relation, relationProxyMethodService, types.toClasses(), CompositeObject.class);
    }

    public <T> T getEntityInstance(Entity entity) {
        Set<EntityDiscriminator> discriminators = getEntityDiscriminators(entity);
        TypeMetadataSet<?> types = metadataProvider.getTypes(discriminators);
        EntityId id = datastoreSession.getId(entity);
        return (T) getOrCreateInstance(entityCache, id, entity, entityProxyMethodService, types.toClasses(), CompositeObject.class);
    }

    private Set<EntityDiscriminator> getEntityDiscriminators(Entity entity) {
        Set<EntityDiscriminator> discriminators = datastoreSession.getEntityDiscriminators(entity);
        if (discriminators == null || discriminators.isEmpty()) {
            throw new CdoException("Cannot determine type discriminators for entity '" + entity + "'");
        }
        return discriminators;
    }

    private <CacheId> Object getOrCreateInstance(TransactionalCache<CacheId> cache, CacheId id, Object e, ProxyMethodService<?, ?> proxyMethodService, Set<Class<?>> types, Class<?>... baseTypes) {
        Object instance = cache.get(id);
        if (instance == null) {
            InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(e, proxyMethodService);
            instance = proxyFactory.createInstance(invocationHandler, types, baseTypes);
            cache.put(id, instance);
        }
        return instance;
    }

    public <Instance> void removeEntityInstance(Instance instance) {
        Entity entity = getEntity(instance);
        EntityId id = datastoreSession.getId(entity);
        entityCache.remove(id);
    }

    public <Instance> void removeRelationInstance(Instance instance) {
        Relation  relation = getRelation(instance);
        RelationId id = datastoreSession.getRelationId(relation);
        relationCache.remove(id);
    }

    public <Instance> void destroyInstance(Instance instance) {
        proxyFactory.getInvocationHandler(instance).close();
    }

    public <Instance> boolean isEntity(Instance instance) {
        if (proxyFactory.isDatastoreType(instance)) {
            return datastoreSession.isEntity(proxyFactory.getInvocationHandler(instance).getDatastoreType());
        }
        return false;
    }

    public <Instance> boolean isRelation(Instance instance) {
        if (proxyFactory.isDatastoreType(instance)) {
            return datastoreSession.isRelation(proxyFactory.getInvocationHandler(instance).getDatastoreType());
        }
        return false;
    }

    public <Instance, DatastoreType> Entity getEntity(Instance instance) {
        InstanceInvocationHandler<DatastoreType> invocationHandler = proxyFactory.getInvocationHandler(instance);
        DatastoreType datastoreType = invocationHandler.getDatastoreType();
        if (!datastoreSession.isEntity(datastoreType)) {
            throw new CdoException(datastoreType + " is not an entity.");
        }
        return (Entity) datastoreType;
    }

    public <Instance, DatastoreType> Relation getRelation(Instance instance) {
        InstanceInvocationHandler<DatastoreType> invocationHandler = proxyFactory.getInvocationHandler(instance);
        DatastoreType datastoreType = invocationHandler.getDatastoreType();
        if (!datastoreSession.isRelation(datastoreType)) {
            throw new CdoException(datastoreType + " is not a relation.");
        }
        return (Relation) datastoreType;
    }

    public void close() {
        destroyInstances(relationCache);
        destroyInstances(entityCache);
    }

    private void destroyInstances(TransactionalCache<?> cache) {
        for (Object instance : cache.values()) {
            destroyInstance(instance);
        }
        cache.clear();
    }


}
