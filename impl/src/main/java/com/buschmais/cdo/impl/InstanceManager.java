package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.proxy.ProxyMethodService;
import com.buschmais.cdo.impl.proxy.interceptor.CdoInterceptor;
import com.buschmais.cdo.impl.proxy.interceptor.InterceptorFactory;
import com.buschmais.cdo.impl.proxy.interceptor.TransactionInterceptor;
import com.buschmais.cdo.spi.metadata.MetadataProvider;
import com.buschmais.cdo.impl.proxy.instance.InstanceInvocationHandler;
import com.buschmais.cdo.impl.proxy.instance.EntityProxyMethodService;
import com.buschmais.cdo.spi.datastore.DatastoreSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.buschmais.cdo.api.CdoManagerFactory.TransactionAttribute;

public class InstanceManager<EntityId, Entity> {

    private final MetadataProvider metadataProvider;
    private final DatastoreSession<EntityId, Entity, ?, ?> datastoreSession;
    private final ClassLoader classLoader;
    private final TransactionalCache cache;
    private final ProxyMethodService<Entity, ?> proxyMethodService;
    private final CdoTransaction cdoTransaction;
    private final TransactionAttribute transactionAttribute;

    public InstanceManager(CdoTransaction cdoTransaction, MetadataProvider metadataProvider, DatastoreSession<EntityId, Entity, ?, ?> datastoreSession, ClassLoader classLoader, TransactionalCache cache, TransactionAttribute transactionAttribute) {
        this.metadataProvider = metadataProvider;
        this.datastoreSession = datastoreSession;
        this.classLoader = classLoader;
        this.cache = cache;
        this.cdoTransaction = cdoTransaction;
        this.transactionAttribute = transactionAttribute;
        PropertyManager propertyManager = new PropertyManager(datastoreSession);
        proxyMethodService = new EntityProxyMethodService(metadataProvider, this, propertyManager, cdoTransaction, transactionAttribute, datastoreSession);
    }

    public <T> T getInstance(Entity entity) {
        Set<Class<?>> types = metadataProvider.getDatastoreMetadataProvider().getTypes(entity);
        EntityId id = datastoreSession.getId(entity);
        Object instance = cache.get(id);
        if (instance == null) {
            InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(entity, proxyMethodService);
            instance = createInstance(invocationHandler, types, CompositeObject.class);
            cache.put(id, instance);
        }
        return (T) instance;
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
        cache.remove(id);
    }

    public <Instance> void destroyInstance(Instance instance) {
        getInvocationHandler(instance).close();
    }

    public <Instance> boolean isEntity(Instance instance) {
        return Proxy.isProxyClass(instance.getClass()) && Proxy.getInvocationHandler(instance) instanceof CdoInterceptor;
    }

    public <Instance> Entity getEntity(Instance instance) {
        InstanceInvocationHandler<Entity> invocationHandler = getInvocationHandler(instance);
        return invocationHandler.getEntity();
    }

    public void close() {
        for (Object instance : cache.values()) {
            destroyInstance(instance);
        }
        cache.clear();
    }

    private Object createProxyInstance(InvocationHandler invocationHandler, List<Class<?>> effectiveTypes) {
        Object instance = Proxy.newProxyInstance(classLoader, effectiveTypes.toArray(new Class<?>[effectiveTypes.size()]), invocationHandler);
        return InterceptorFactory.addInterceptor(instance, new TransactionInterceptor(instance, cdoTransaction, transactionAttribute));
    }

    private <Instance> InstanceInvocationHandler<Entity> getInvocationHandler(Instance instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(InterceptorFactory.removeInterceptor(instance, TransactionInterceptor.class));
        if (!(invocationHandler instanceof InstanceInvocationHandler)) {
            throw new CdoException("Instance " + instance + " implementing " + Arrays.asList(instance.getClass().getInterfaces()) + " is not a " + InstanceInvocationHandler.class.getName());
        }
        return (InstanceInvocationHandler<Entity>) invocationHandler;
    }

}
