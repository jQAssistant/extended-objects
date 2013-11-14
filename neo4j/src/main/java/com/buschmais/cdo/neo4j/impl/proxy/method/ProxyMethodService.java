package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.api.CdoManagerException;
import com.buschmais.cdo.neo4j.impl.metadata.*;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import org.neo4j.graphdb.Node;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProxyMethodService {

    private final Map<Method, ProxyMethod> proxyMethods;

    public ProxyMethodService(NodeMetadataProvider nodeMetadataProvider, InstanceManager instanceManager) {
        proxyMethods = new HashMap<>();
        for (NodeMetadata nodeMetadata : nodeMetadataProvider.getRegisteredNodeMetadata()) {
            for (AbstractPropertyMetadata propertyMetadata : nodeMetadata.getProperties()) {
                Method getter = propertyMetadata.getBeanProperty().getGetter();
                if (getter != null) {
                    ProxyMethod getterProxyMethod;
                    if (propertyMetadata instanceof PrimitivePropertyMetadata) {
                        getterProxyMethod = new PrimitivePropertyGetMethod((PrimitivePropertyMetadata) propertyMetadata, instanceManager);
                    } else if (propertyMetadata instanceof EnumPropertyMetadata) {
                        getterProxyMethod = new EnumPropertyGetMethod((EnumPropertyMetadata) propertyMetadata, instanceManager);
                    } else if (propertyMetadata instanceof ReferencePropertyMetadata) {
                        getterProxyMethod = new ReferencePropertyGetMethod((ReferencePropertyMetadata) propertyMetadata, instanceManager);
                    } else if (propertyMetadata instanceof CollectionPropertyMetadata) {
                        getterProxyMethod = new CollectionPropertyGetMethod((CollectionPropertyMetadata) propertyMetadata, instanceManager);
                    } else {
                        throw new CdoManagerException("Unsupported metadata type " + propertyMetadata);
                    }
                    addProxyMethod(getterProxyMethod, getter);
                }
                Method setter = propertyMetadata.getBeanProperty().getSetter();
                if (setter != null) {
                    ProxyMethod setterProxyMethod;
                    if (propertyMetadata instanceof PrimitivePropertyMetadata) {
                        setterProxyMethod = new PrimitivePropertySetMethod((PrimitivePropertyMetadata) propertyMetadata, instanceManager);
                    } else if (propertyMetadata instanceof EnumPropertyMetadata) {
                        setterProxyMethod = new EnumPropertySetMethod((EnumPropertyMetadata) propertyMetadata, instanceManager);
                    } else if (propertyMetadata instanceof ReferencePropertyMetadata) {
                        setterProxyMethod = new ReferencePropertySetMethod((ReferencePropertyMetadata) propertyMetadata, instanceManager);
                    } else if (propertyMetadata instanceof CollectionPropertyMetadata) {
                        setterProxyMethod = new CollectionPropertySetMethod((CollectionPropertyMetadata) propertyMetadata, instanceManager);
                    } else {
                        throw new CdoManagerException("Unsupported metadata type " + propertyMetadata);
                    }
                    addProxyMethod(setterProxyMethod, setter);
                }
            }
        }
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
