package com.buschmais.xo.impl;

import com.buschmais.xo.api.CompositeObject;
import com.buschmais.xo.impl.cache.TransactionalCache;
import com.buschmais.xo.impl.instancelistener.InstanceListenerService;
import com.buschmais.xo.impl.proxy.InstanceInvocationHandler;
import com.buschmais.xo.impl.proxy.ProxyMethodService;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;

/**
 * Abstract base implementation of an instance manager.
 * <p>It provides functionality to map the lifecycle of proxy instances to their corresponding datastore type.</p>
 *
 * @param <DatastoreId>   The id type of the datastore type.
 * @param <DatastoreType> The datastore type.
 */
public abstract class AbstractInstanceManager<DatastoreId, DatastoreType> {

    private final TransactionalCache<DatastoreId> cache;
    private final InstanceListenerService instanceListenerService;
    private final ProxyFactory proxyFactory;


    /**
     * Constructor.
     *
     * @param cache        The transactional cache.
     * @param proxyFactory The proxy factory.
     */
    public AbstractInstanceManager(TransactionalCache<DatastoreId> cache, InstanceListenerService instanceListenerService, ProxyFactory proxyFactory) {
        this.cache = cache;
        this.instanceListenerService = instanceListenerService;
        this.proxyFactory = proxyFactory;
    }

    /**
     * Return the proxy instance which corresponds to the given datastore type for reading.
     *
     * @param datastoreType The datastore type.
     * @param <T>           The instance type.
     * @return The instance.
     */
    public <T> T readInstance(DatastoreType datastoreType) {
        return getInstance(datastoreType, TransactionalCache.Mode.READ);
    }

    /**
     * Return the proxy instance which corresponds to the given datastore type for writing.
     *
     * @param datastoreType The datastore type.
     * @param <T>           The instance type.
     * @return The instance.
     */
    public <T> T createInstance(DatastoreType datastoreType) {
        return getInstance(datastoreType, TransactionalCache.Mode.WRITE);
    }

    public <T> T updateInstance(DatastoreType datastoreType) {
        return getInstance(datastoreType, TransactionalCache.Mode.WRITE);
    }

    /**
     * Return the proxy instance which corresponds to the given datastore type.
     *
     * @param datastoreType The datastore type.
     * @param <T>           The instance type.
     * @return The instance.
     */
    private <T> T getInstance(DatastoreType datastoreType, TransactionalCache.Mode cacheMode) {
        DatastoreId id = getDatastoreId(datastoreType);
        Object instance = cache.get(id, cacheMode);
        if (instance == null) {
            InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(datastoreType, getProxyMethodService());
            TypeMetadataSet<?> types = getTypes(datastoreType);
            instance = proxyFactory.createInstance(invocationHandler, types.toClasses(), CompositeObject.class);
            cache.put(id, instance, cacheMode);
            if (TransactionalCache.Mode.READ.equals(cacheMode)) {
                instanceListenerService.postLoad(instance);
            }
        }
        return (T) instance;
    }

    /**
     * Removes an instance, e.g. before deletion or migration.
     *
     * @param instance   The instance.
     * @param <Instance> The instance type.
     */
    public <Instance> void removeInstance(Instance instance) {
        DatastoreType datastoreType = getDatastoreType(instance);
        DatastoreId id = getDatastoreId(datastoreType);
        cache.remove(id);
    }

    /**
     * Destroys an instance, i.e. makes it unusable-
     *
     * @param instance   The instance.
     * @param <Instance> The instance type.
     */
    public <Instance> void closeInstance(Instance instance) {
        proxyFactory.getInvocationHandler(instance).close();
    }

    /**
     * Determine if a given instance is a datastore type handled by this manager.
     *
     * @param instance   The instance.
     * @param <Instance> The instance type.
     * @return <code>true</code> if the instance is handled by this manager.
     */
    public <Instance> boolean isInstance(Instance instance) {
        if (instance instanceof CompositeObject) {
            Object delegate = ((CompositeObject) instance).getDelegate();
            return isDatastoreType(delegate);
        }
        return false;
    }

    /**
     * Return the datastore type represented by an instance.
     *
     * @param instance   The instance.
     * @param <Instance> The instance type.
     * @return The corresponding datastore type.
     */
    public <Instance> DatastoreType getDatastoreType(Instance instance) {
        InstanceInvocationHandler<DatastoreType> invocationHandler = proxyFactory.getInvocationHandler(instance);
        return invocationHandler.getDatastoreType();
    }

    /**
     * Return the unique id of a datastore type.
     *
     * @param datastoreType The datastore type.
     * @return The id.
     */
    public abstract DatastoreId getDatastoreId(DatastoreType datastoreType);

    /**
     * Closes this manager instance.
     */
    public void close() {
        for (Object instance : cache.readInstances()) {
            closeInstance(instance);
        }
        cache.clear();
    }

    /**
     * Determine if a given object is a datastore type.
     *
     * @param o The object
     * @return <code>true</code> If the given object is a datastore type.
     */
    protected abstract boolean isDatastoreType(Object o);

    /**
     * Determines the {@link com.buschmais.xo.spi.datastore.TypeMetadataSet} of a datastore type.
     *
     * @param datastoreType The datastore type.
     * @return The {@link com.buschmais.xo.spi.datastore.TypeMetadataSet}.
     */
    protected abstract TypeMetadataSet<?> getTypes(DatastoreType datastoreType);

    /**
     * Return the {@link com.buschmais.xo.impl.proxy.ProxyMethodService} associated with this manager.
     *
     * @return The {@link com.buschmais.xo.impl.proxy.ProxyMethodService}.
     */
    protected abstract ProxyMethodService<DatastoreType> getProxyMethodService();
}
