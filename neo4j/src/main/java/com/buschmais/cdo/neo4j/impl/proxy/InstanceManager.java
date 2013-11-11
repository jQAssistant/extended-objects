package com.buschmais.cdo.neo4j.impl.proxy;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.proxy.method.ProxyMethodService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class InstanceManager {

    private final NodeMetadataProvider nodeMetadataProvider;
    private final ClassLoader classLoader;
    private final Map<Long, Object> instanceCache;
    private final ProxyMethodService proxyMethodService;

    public InstanceManager(NodeMetadataProvider nodeMetadataProvider, ClassLoader classLoader) {
        this.nodeMetadataProvider = nodeMetadataProvider;
        this.classLoader = classLoader;
        instanceCache = new WeakHashMap<>();
        proxyMethodService = new ProxyMethodService(nodeMetadataProvider, this);
    }

    public <T> T getInstance(Node node) {
        Class<T> type = getType(node);
        return getInstance(node, type);
    }

    public <T> Class<T> getType(Node node) {
        Set<Label> labels = new HashSet<>();
        for (Label label : node.getLabels()) {
            labels.add(label);
        }
        labels.retainAll(nodeMetadataProvider.getAllLabels());
        NodeMetadata nodeMetadata = nodeMetadataProvider.getNodeMetadata(labels);
        return (Class<T>) nodeMetadata.getType();
    }

    public <T> T getInstance(Node node, Class<T> type) {
        Object instance = instanceCache.get(Long.valueOf(node.getId()));
        if (instance == null) {
            NodeInvocationHandler invocationHandler = new NodeInvocationHandler(node, proxyMethodService);
            instance = Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, invocationHandler);
            instanceCache.put(Long.valueOf(node.getId()), instance);
        }
        return type.cast(instance);
    }

    public <T> void removeInstance(T instance) {
        Node node = getNode(instance);
        instanceCache.remove(Long.valueOf(node.getId()));
    }

    public <T> void destroyInstance(T instance) {
        getInvocationHandler(instance).close();
    }

    public <T> boolean isNode(T instance) {
        return Proxy.isProxyClass(instance.getClass()) && Proxy.getInvocationHandler(instance) instanceof NodeInvocationHandler;
    }

    public <T> Node getNode(T instance) {
        NodeInvocationHandler invocationHandler = getInvocationHandler(instance);
        return invocationHandler.getNode();
    }

    public void close() {
        for (Object instance : instanceCache.values()) {
            destroyInstance(instance);
        }
        instanceCache.clear();
    }

    private <T> NodeInvocationHandler getInvocationHandler(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!(invocationHandler instanceof NodeInvocationHandler)) {
            throw new CdoManagerException("Instance " + instance + " is not a " + NodeInvocationHandler.class.getName());
        }
        return (NodeInvocationHandler) invocationHandler;
    }

}
