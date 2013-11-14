package com.buschmais.cdo.neo4j.impl.proxy;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.proxy.method.ProxyMethodService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

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
        List<Class<?>> types = getTypes(node);
        return getInstance(node, types);
    }

    public List<Class<?>> getTypes(Node node) {
        Set<Class<?>> types = new HashSet<>();
        for (Label label : node.getLabels()) {
            NodeMetadata nodeMetadata = nodeMetadataProvider.getNodeMetadata(label);
            if (nodeMetadata != null) {
                types.add(nodeMetadata.getType());
            }
        }
        SortedSet<Class<?>> uniqueTypes = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Class<?> type : types) {
            boolean subtype = false;
            for (Iterator<Class<?>> subTypeIterator = types.iterator(); subTypeIterator.hasNext() && !subtype; ) {
                Class<?> otherType = subTypeIterator.next();
                if (!type.equals(otherType) && type.isAssignableFrom(otherType)) {
                    subtype = true;
                }
            }
            if (!subtype) {
                uniqueTypes.add(type);
            }
        }
        return new ArrayList<>(uniqueTypes);
    }

    public <T> T getInstance(Node node, List<Class<?>> types) {
        Object instance = instanceCache.get(Long.valueOf(node.getId()));
        if (instance == null) {
            NodeInvocationHandler invocationHandler = new NodeInvocationHandler(node, proxyMethodService);
            List<Class<?>> effectiveTypes = new ArrayList<>(types.size() +1);
            effectiveTypes.addAll(types);
            effectiveTypes.add(CompositeObject.class);
            instance = Proxy.newProxyInstance(classLoader, effectiveTypes.toArray(new Class<?>[effectiveTypes.size()]), invocationHandler);
            instanceCache.put(Long.valueOf(node.getId()), instance);
        }
        return (T) instance;
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
            throw new CdoException("Instance " + instance + " is not a " + NodeInvocationHandler.class.getName());
        }
        return (NodeInvocationHandler) invocationHandler;
    }

}
