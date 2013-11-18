package com.buschmais.cdo.neo4j.impl.node;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.impl.cache.TransactionalCache;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadata;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.impl.node.proxy.NodeInvocationHandler;
import com.buschmais.cdo.neo4j.impl.node.proxy.method.NodeProxyMethodService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

public class InstanceManager {

    private final NodeMetadataProvider nodeMetadataProvider;
    private final ClassLoader classLoader;
    private final TransactionalCache cache;
    private final NodeProxyMethodService nodeProxyMethodService;

    public InstanceManager(NodeMetadataProvider nodeMetadataProvider, GraphDatabaseService graphDatabaseService, ClassLoader classLoader, TransactionalCache cache) {
        this.nodeMetadataProvider = nodeMetadataProvider;
        this.classLoader = classLoader;
        this.cache = cache;
        nodeProxyMethodService = new NodeProxyMethodService(nodeMetadataProvider, this, graphDatabaseService);
    }

    public <T> T getInstance(Node node) {
        List<Class<?>> types = getTypes(node);
        return getInstance(node, types);
    }

    public List<Class<?>> getTypes(Node node) {
        // Collect all labels from the node
        Set<Label> labels = new HashSet<>();
        for (Label label : node.getLabels()) {
            labels.add(label);
        }
        // Get all types matching the labels
        Set<Class<?>> types = new HashSet<>();
        for (Label label : labels) {
            Set<NodeMetadata> nodeMetadataOfLabel = nodeMetadataProvider.getNodeMetadata(label);
            if (nodeMetadataOfLabel != null) {
                for (NodeMetadata nodeMetadata : nodeMetadataOfLabel) {
                    if (labels.containsAll(nodeMetadata.getAggregatedLabels())) {
                        types.add(nodeMetadata.getType());
                    }
                }
            }
        }
        SortedSet<Class<?>> uniqueTypes = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        // Remove super types if subtypes are already in the type set
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
        Object instance = cache.get(Long.valueOf(node.getId()));
        if (instance == null) {
            NodeInvocationHandler invocationHandler = new NodeInvocationHandler(node, nodeProxyMethodService);
            instance = createInstance(invocationHandler, types, CompositeObject.class);
            cache.put(Long.valueOf(node.getId()), instance);
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
        Node node = getNode(instance);
        cache.remove(Long.valueOf(node.getId()));
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
        for (Object instance : cache.values()) {
            destroyInstance(instance);
        }
        cache.clear();
    }

    private <T> NodeInvocationHandler getInvocationHandler(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!(invocationHandler instanceof NodeInvocationHandler)) {
            throw new CdoException("Instance " + instance + " is not a " + NodeInvocationHandler.class.getName());
        }
        return (NodeInvocationHandler) invocationHandler;
    }

}
