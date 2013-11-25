package com.buschmais.cdo.neo4j.impl.node;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.impl.cache.TransactionalCache;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.node.proxy.InstanceInvocationHandler;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.NodeProxyMethodService;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstanceManager<I, E> {

    //private final NodeMetadataProvider nodeMetadataProvider;
    private final DatastoreSession<I, E> datastoreSession;
    private final ClassLoader classLoader;
    private final TransactionalCache cache;
    private final NodeProxyMethodService proxyMethodService;

    public InstanceManager(NodeMetadataProvider metadataProvider, DatastoreSession<I, E> datastoreSession, ClassLoader classLoader, TransactionalCache cache) {
        this.datastoreSession = datastoreSession;
        this.classLoader = classLoader;
        this.cache = cache;
        proxyMethodService = new NodeProxyMethodService(metadataProvider, this, datastoreSession);
    }

    public <T> T getInstance(E entity) {
         List<Class<?>> types = datastoreSession.getTypes(entity);
        I id = datastoreSession.getId(entity);
        Object instance = cache.get(id);
        if (instance == null) {
            InstanceInvocationHandler invocationHandler = new InstanceInvocationHandler(entity, proxyMethodService);
            instance = createInstance(invocationHandler, types, CompositeObject.class);
            cache.put(id, instance);
        }
        return (T) instance;
    }

    public <T> T createInstance(InvocationHandler invocationHandler, List<Class<?>> types, Class<?>... baseTypes) {
        Object instance;
        List<Class<?>> effectiveTypes = new ArrayList<>(types.size() + baseTypes.length);
        effectiveTypes.addAll(types);
        effectiveTypes.addAll(Arrays.asList(baseTypes));
        instance = Proxy.newProxyInstance(classLoader, effectiveTypes.toArray(new Class<?>[effectiveTypes.size()]), invocationHandler);
        return (T) instance;
    }

    public <T> void removeInstance(T instance) {
        E entity = getNode(instance);
        I id = datastoreSession.getId(entity);
        cache.remove(id);
    }

    public <T> void destroyInstance(T instance) {
        getInvocationHandler(instance).close();
    }

    public <T> boolean isNode(T instance) {
        return Proxy.isProxyClass(instance.getClass()) && Proxy.getInvocationHandler(instance) instanceof InstanceInvocationHandler;
    }

    public <T> E getNode(T instance) {
        InstanceInvocationHandler<E> invocationHandler = getInvocationHandler(instance);
        return invocationHandler.getEntity();
    }

    public void close() {
        for (Object instance : cache.values()) {
            destroyInstance(instance);
        }
        cache.clear();
    }

    private <T> InstanceInvocationHandler<E> getInvocationHandler(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!(invocationHandler instanceof InstanceInvocationHandler)) {
            throw new CdoException("Instance " + instance + " is not a " + InstanceInvocationHandler.class.getName());
        }
        return (InstanceInvocationHandler<E>) invocationHandler;
    }

}
