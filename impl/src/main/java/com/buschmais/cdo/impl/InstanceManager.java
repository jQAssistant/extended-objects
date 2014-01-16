package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.proxy.ProxyMethodService;
import com.buschmais.cdo.impl.proxy.entity.InstanceInvocationHandler;
import com.buschmais.cdo.spi.datastore.TypeMetadataSet;

import java.util.Set;

public abstract class InstanceManager<DatastoreId, DatastoreType> {

    private final TransactionalCache<DatastoreId> cache;
    private final ProxyFactory proxyFactory;

    public InstanceManager(TransactionalCache<DatastoreId> cache, ProxyFactory proxyFactory) {
        this.cache = cache;
        this.proxyFactory = proxyFactory;
    }

    public <T> T getInstance(DatastoreType datastoreType) {
        DatastoreId id = getDatastoreId(datastoreType);
        TypeMetadataSet<?> types = getTypes(datastoreType);
        return (T) getOrCreateInstance(id, datastoreType, types.toClasses(), CompositeObject.class);
    }

    private Object getOrCreateInstance(DatastoreId id, Object e, Set<Class<?>> types, Class<?>... baseTypes) {
        Object instance = cache.get(id);
        if (instance == null) {
            InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(e, getProxyMethodService());
            instance = proxyFactory.createInstance(invocationHandler, types, baseTypes);
            cache.put(id, instance);
        }
        return instance;
    }

    public <Instance> void removeInstance(Instance instance) {
        DatastoreType datastoreType = getDatastoreType(instance);
        DatastoreId id = getDatastoreId(datastoreType);
        cache.remove(id);
    }

    public <Instance> void destroyInstance(Instance instance) {
        proxyFactory.getInvocationHandler(instance).close();
    }

    public <Instance> boolean isInstance(Instance instance) {
        if (proxyFactory.isDatastoreType(instance)) {
            return isDatastoreType(proxyFactory.getInvocationHandler(instance).getDatastoreType());
        }
        return false;
    }

    public <Instance> DatastoreType getDatastoreType(Instance instance) {
        InstanceInvocationHandler<DatastoreType> invocationHandler = proxyFactory.getInvocationHandler(instance);
        DatastoreType datastoreType = invocationHandler.getDatastoreType();
        return datastoreType;
    }

    public void close() {
        destroyInstances(cache);
    }

    public abstract boolean isDatastoreType(Object o);

    protected abstract DatastoreId getDatastoreId(DatastoreType datastoreType);

    protected abstract TypeMetadataSet<?> getTypes(DatastoreType datastoreType);

    protected abstract ProxyMethodService<DatastoreType, ?> getProxyMethodService();

    private void destroyInstances(TransactionalCache<?> cache) {
        for (Object instance : cache.values()) {
            destroyInstance(instance);
        }
        cache.clear();
    }


}
