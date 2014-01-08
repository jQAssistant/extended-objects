package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.interceptor.CdoInterceptor;
import com.buschmais.cdo.impl.proxy.ProxyMethodService;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
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
    private final ClassLoader classLoader;
    private final TransactionalCache<EntityId> entityCache;
    private final TransactionalCache<RelationId> relationCache;
    private final ProxyMethodService<Entity, ?> entityProxyMethodService;
    private final InterceptorFactory interceptorFactory;

    public InstanceManager(MetadataProvider metadataProvider, DatastoreSession<EntityId, Entity, ?, EntityDiscriminator, RelationId, Relation, ?, RelationDiscriminator> datastoreSession, ClassLoader classLoader, CdoTransaction cdoTransaction, TransactionalCache<EntityId> entityCache, TransactionalCache relationCache, InterceptorFactory interceptorFactory) {
        this.metadataProvider = metadataProvider;
        this.datastoreSession = datastoreSession;
        this.classLoader = classLoader;
        this.entityCache = entityCache;
        this.relationCache = relationCache;
        PropertyManager propertyManager = new PropertyManager(datastoreSession);
        this.interceptorFactory = interceptorFactory;
        entityProxyMethodService = new EntityProxyMethodService(metadataProvider, this, propertyManager, cdoTransaction, interceptorFactory, datastoreSession);
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
        return (T) getOrCreateInstance(relationCache, id, relation, types.toClasses(), CompositeObject.class);
    }

    public <T> T getEntityInstance(Entity entity) {
        Set<EntityDiscriminator> discriminators = getEntityDiscriminators(entity);
        TypeMetadataSet<?> types = metadataProvider.getTypes(discriminators);
        EntityId id = datastoreSession.getId(entity);
        return (T) getOrCreateInstance(entityCache, id, entity, types.toClasses(), CompositeObject.class);
    }

    private Set<EntityDiscriminator> getEntityDiscriminators(Entity entity) {
        Set<EntityDiscriminator> discriminators = datastoreSession.getEntityDiscriminators(entity);
        if (discriminators == null || discriminators.isEmpty()) {
            throw new CdoException("Cannot determine type discriminators for entity '" + entity + "'");
        }
        return discriminators;
    }

    private <CacheId> Object getOrCreateInstance(TransactionalCache<CacheId> cache, CacheId id, Object e, Set<Class<?>> types, Class<?>... baseTypes) {
        Object instance = cache.get(id);
        if (instance == null) {
            InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(e, entityProxyMethodService);
            instance = createInstance(invocationHandler, types, baseTypes);
            cache.put(id, instance);
        }
        return instance;
    }

    public <Instance> Instance createInstance(InvocationHandler invocationHandler, Set<Class<?>> types, Class<?>... baseTypes) {
        List<Class<?>> effectiveTypes = new ArrayList<>(types.size() + baseTypes.length);
        effectiveTypes.addAll(types);
        effectiveTypes.addAll(Arrays.asList(baseTypes));
        return (Instance) createProxyInstance(invocationHandler, effectiveTypes);
    }

    public <Instance> void removeInstance(Instance instance) {
        Entity entity = getEntity(instance);
        EntityId id = datastoreSession.getId(entity);
        entityCache.remove(id);
    }

    public <Instance> void destroyInstance(Instance instance) {
        getInvocationHandler(instance).close();
    }

    public <Instance> boolean isEntity(Instance instance) {
        if (isDatastoreType((Instance) instance)) {
            return datastoreSession.isEntity(getInvocationHandler(instance).getDatastoreType());
        }
        return false;
    }

    public <Instance> boolean isRelation(Instance instance) {
        if (isDatastoreType(instance)) {
            return datastoreSession.isRelation(getInvocationHandler(instance).getDatastoreType());
        }
        return false;
    }


    public <Instance, DatastoreType> Entity getEntity(Instance instance) {
        InstanceInvocationHandler<DatastoreType> invocationHandler = getInvocationHandler(instance);
        DatastoreType datastoreType = invocationHandler.getDatastoreType();
        if (!datastoreSession.isEntity(datastoreType)) {
            throw new CdoException(datastoreType + " is not an entity.");
        }
        return (Entity) datastoreType;
    }

    public <Instance, DatastoreType> Relation getRelation(Instance instance) {
        InstanceInvocationHandler<DatastoreType> invocationHandler = getInvocationHandler(instance);
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

    private Object createProxyInstance(InvocationHandler invocationHandler, List<Class<?>> effectiveTypes) {
        Object instance = Proxy.newProxyInstance(classLoader, effectiveTypes.toArray(new Class<?>[effectiveTypes.size()]), invocationHandler);
        return interceptorFactory.addInterceptor(instance);
    }

    private <Instance> boolean isDatastoreType(Instance instance) {
        return Proxy.isProxyClass(instance.getClass()) && Proxy.getInvocationHandler(instance) instanceof CdoInterceptor;
    }

    private <DatastoreType, Instance> InstanceInvocationHandler<DatastoreType> getInvocationHandler(Instance instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(interceptorFactory.removeInterceptor(instance));
        if (!(invocationHandler instanceof InstanceInvocationHandler)) {
            throw new CdoException("Instance " + instance + " implementing " + Arrays.asList(instance.getClass().getInterfaces()) + " is not a " + InstanceInvocationHandler.class.getName());
        }
        return (InstanceInvocationHandler<DatastoreType>) invocationHandler;
    }
}
