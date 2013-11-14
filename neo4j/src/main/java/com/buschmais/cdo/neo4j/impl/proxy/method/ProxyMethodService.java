package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.impl.metadata.*;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.proxy.method.composite.AsMethod;
import com.buschmais.cdo.neo4j.impl.proxy.method.property.*;
import com.buschmais.cdo.neo4j.impl.proxy.method.object.EqualsMethod;
import com.buschmais.cdo.neo4j.impl.proxy.method.object.HashCodeMethod;
import com.buschmais.cdo.neo4j.impl.proxy.method.object.ToStringMethod;
import org.neo4j.graphdb.Node;

import java.lang.management.MemoryType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProxyMethodService {

    private final Map<Method, ProxyMethod> proxyMethods;

    public ProxyMethodService(NodeMetadataProvider nodeMetadataProvider, InstanceManager instanceManager) {
        proxyMethods = new HashMap<>();
        for (NodeMetadata nodeMetadata : nodeMetadataProvider.getRegisteredNodeMetadata()) {
            for (AbstractMethodMetadata methodMetadata : nodeMetadata.getProperties()) {
                BeanPropertyMethod beanMethod = methodMetadata.getBeanMethod();
                ProxyMethod proxyMethod = null;
                if (methodMetadata instanceof PrimitiveMethodMetadata) {
                    switch (beanMethod.getMethodType()) {
                        case GETTER:
                            proxyMethod = new PrimitivePropertyGetMethod((PrimitiveMethodMetadata) methodMetadata, instanceManager);
                            break;
                        case SETTER:
                            proxyMethod = new PrimitivePropertySetMethod((PrimitiveMethodMetadata) methodMetadata, instanceManager);
                            break;
                    }
                } else if (methodMetadata instanceof EnumMethodMetadata) {
                    switch (beanMethod.getMethodType()) {
                        case GETTER:
                            proxyMethod = new EnumPropertyGetMethod((EnumMethodMetadata) methodMetadata, instanceManager);
                            break;
                        case SETTER:
                            proxyMethod = new EnumPropertySetMethod((EnumMethodMetadata) methodMetadata, instanceManager);
                            break;
                    }
                } else if (methodMetadata instanceof ReferenceMethodMetadata) {
                    switch (beanMethod.getMethodType()) {
                        case GETTER:
                            proxyMethod = new ReferencePropertyGetMethod((ReferenceMethodMetadata) methodMetadata, instanceManager);
                            break;
                        case SETTER:
                            proxyMethod = new ReferencePropertySetMethod((ReferenceMethodMetadata) methodMetadata, instanceManager);
                            break;
                    }
                } else if (methodMetadata instanceof CollectionMethodMetadata) {
                    switch (beanMethod.getMethodType()) {
                        case GETTER:
                            proxyMethod = new CollectionPropertyGetMethod((CollectionMethodMetadata) methodMetadata, instanceManager);
                            break;
                        case SETTER:
                            proxyMethod = new CollectionPropertySetMethod((CollectionMethodMetadata) methodMetadata, instanceManager);
                            break;
                    }
                }
                if (proxyMethod == null) {
                    throw new CdoManagerException("Unsupported metadata type " + methodMetadata);
                }
                addProxyMethod(proxyMethod, beanMethod.getMethod());
            }
        }
        addMethod(new AsMethod(), CompositeObject.class, "as", Class.class);
        addMethod(new HashCodeMethod(), Object.class, "hashCode");
        addMethod(new EqualsMethod(instanceManager), Object.class, "equals", Object.class);
        addMethod(new ToStringMethod(instanceManager), Object.class, "toString");
    }

    public Object invoke(Node node, Object instance, Method method, Object[] args) {
        ProxyMethod proxyMethod = proxyMethods.get(method);
        if (proxyMethod == null) {
            throw new CdoManagerException("Cannot invoke method " + method.getName());
        }
        return proxyMethod.invoke(node, instance, args);
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
}
