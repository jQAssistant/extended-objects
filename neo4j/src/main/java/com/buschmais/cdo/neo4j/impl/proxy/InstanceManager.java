package com.buschmais.cdo.neo4j.impl.proxy;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.impl.metadata.*;
import com.buschmais.cdo.neo4j.impl.proxy.method.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class InstanceManager {

    private final NodeMetadataProvider nodeMetadataProvider;
    private final ClassLoader classLoader;
    private final Map<Long, Object> instanceCache;
    private final Map<Method, ProxyMethod> proxyMethods;

    public InstanceManager(NodeMetadataProvider nodeMetadataProvider, ClassLoader classLoader) {
        this.nodeMetadataProvider = nodeMetadataProvider;
        this.classLoader = classLoader;
        instanceCache = new WeakHashMap<>();
        proxyMethods = new HashMap<>();
        for (NodeMetadata nodeMetadata : nodeMetadataProvider.getRegisteredNodeMetadata()) {
            for (AbstractPropertyMetadata propertyMetadata : nodeMetadata.getProperties().values()) {
                Method getter = propertyMetadata.getBeanProperty().getGetter();
                if (getter != null) {
                    if (propertyMetadata instanceof PrimitivePropertyMetadata) {
                        addProxyMethod(new PrimitivePropertyGetMethod((PrimitivePropertyMetadata) propertyMetadata, this), getter);
                    } else if (propertyMetadata instanceof EnumPropertyMetadata) {
                        addProxyMethod(new EnumPropertyGetMethod((EnumPropertyMetadata) propertyMetadata, this), getter);
                    } else if (propertyMetadata instanceof ReferencePropertyMetadata) {
                        addProxyMethod(new ReferencePropertyGetMethod((ReferencePropertyMetadata) propertyMetadata, this), getter);
                    } else if (propertyMetadata instanceof CollectionPropertyMetadata) {
                        addProxyMethod(new CollectionPropertyGetMethod((CollectionPropertyMetadata) propertyMetadata, this), getter);
                    }
                }
                Method setter = propertyMetadata.getBeanProperty().getSetter();
                if (setter != null) {
                    if (propertyMetadata instanceof PrimitivePropertyMetadata) {
                        addProxyMethod(new PrimitivePropertySetMethod((PrimitivePropertyMetadata) propertyMetadata, this), setter);
                    } else if (propertyMetadata instanceof EnumPropertyMetadata) {
                        addProxyMethod(new EnumPropertySetMethod((EnumPropertyMetadata) propertyMetadata, this), setter);
                    } else if (propertyMetadata instanceof ReferencePropertyMetadata) {
                        addProxyMethod(new ReferencePropertySetMethod((ReferencePropertyMetadata) propertyMetadata, this), setter);
                    } else if (propertyMetadata instanceof CollectionPropertyMetadata) {
                        addProxyMethod(new CollectionPropertySetMethod((CollectionPropertyMetadata) propertyMetadata, this), setter);
                    }
                }
            }
        }
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(this), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(), Object.class, "toString");
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
            NodeInvocationHandler invocationHandler = new NodeInvocationHandler(node, this);
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

    public Object invoke(Node node, Method method, Object[] args) {
        ProxyMethod proxyMethod = proxyMethods.get(method);
        return proxyMethod.invoke(node, args);
    }

    public void close() {
        for (Object instance : instanceCache.values()) {
            destroyInstance(instance);
        }
        instanceCache.clear();
    }

    private void addMethod(ProxyMethod proxyMethod, Class<?> type, String name, Class<?>... argumentTypes) {
        Method method;
        try {
            method = type.getDeclaredMethod(name, argumentTypes);
        } catch (NoSuchMethodException e) {
            throw new CdoManagerException("Cannot resolve method '" + name + "' (" + Arrays.asList(argumentTypes) + ")");
        }
        addProxyMethod(proxyMethod, method);
    }

    private void addProxyMethod(ProxyMethod proxyMethod, Method method) {
        if (method != null) {
            proxyMethods.put(method, proxyMethod);
        }
    }

    private <T> NodeInvocationHandler getInvocationHandler(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!(invocationHandler instanceof NodeInvocationHandler)) {
            throw new CdoManagerException("Instance " + instance + " is not a " + NodeInvocationHandler.class.getName());
        }
        return (NodeInvocationHandler) invocationHandler;
    }

}
