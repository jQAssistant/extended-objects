package com.buschmais.cdo.neo4j.impl.proxy.method;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CompositeObject;
import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;
import com.buschmais.cdo.neo4j.impl.metadata.*;
import com.buschmais.cdo.neo4j.impl.proxy.InstanceManager;
import com.buschmais.cdo.neo4j.impl.proxy.method.composite.AsMethod;
import com.buschmais.cdo.neo4j.impl.proxy.method.object.EqualsMethod;
import com.buschmais.cdo.neo4j.impl.proxy.method.object.HashCodeMethod;
import com.buschmais.cdo.neo4j.impl.proxy.method.object.ToStringMethod;
import com.buschmais.cdo.neo4j.impl.proxy.method.property.*;
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
            for (AbstractMethodMetadata methodMetadata : nodeMetadata.getProperties()) {
                BeanMethod beanMethod = methodMetadata.getBeanMethod();
                ProxyMethod proxyMethod = null;
                if (methodMetadata instanceof InvokeUsingMethodMetadata) {
                    InvokeUsingMethodMetadata invokeUsingMethodMetadata = (InvokeUsingMethodMetadata) methodMetadata;
                    Class<? extends ProxyMethod> proxyMethodType = invokeUsingMethodMetadata.getProxyMethodType();
                    try {
                        proxyMethod = proxyMethodType.newInstance();
                    } catch (InstantiationException e) {
                        throw new CdoException("Cannot instantiate proxy method of type " + proxyMethodType.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new CdoException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
                    }
                }
                if (methodMetadata instanceof AbstractPropertyMethodMetadata) {
                    BeanPropertyMethod beanPropertyMethod = (BeanPropertyMethod) beanMethod;
                    if (methodMetadata instanceof PrimitivePropertyMethodMetadata) {
                        switch (beanPropertyMethod.getMethodType()) {
                            case GETTER:
                                proxyMethod = new PrimitivePropertyGetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                            case SETTER:
                                proxyMethod = new PrimitivePropertySetMethod((PrimitivePropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                        }
                    } else if (methodMetadata instanceof EnumPropertyMethodMetadata) {
                        switch (beanPropertyMethod.getMethodType()) {
                            case GETTER:
                                proxyMethod = new EnumPropertyGetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                            case SETTER:
                                proxyMethod = new EnumPropertySetMethod((EnumPropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                        }
                    } else if (methodMetadata instanceof ReferencePropertyMethodMetadata) {
                        switch (beanPropertyMethod.getMethodType()) {
                            case GETTER:
                                proxyMethod = new ReferencePropertyGetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                            case SETTER:
                                proxyMethod = new ReferencePropertySetMethod((ReferencePropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                        }
                    } else if (methodMetadata instanceof CollectionPropertyMethodMetadata) {
                        switch (beanPropertyMethod.getMethodType()) {
                            case GETTER:
                                proxyMethod = new CollectionPropertyGetMethod((CollectionPropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                            case SETTER:
                                proxyMethod = new CollectionPropertySetMethod((CollectionPropertyMethodMetadata) methodMetadata, instanceManager);
                                break;
                        }
                    }
                }
                if (proxyMethod == null) {
                    throw new CdoException("Unsupported metadata type " + methodMetadata);
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
            throw new CdoException("Cannot invoke method " + method.getName());
        }
        return proxyMethod.invoke(node, instance, args);
    }

    private void addMethod(ProxyMethod proxyMethod, Class<?> type, String name, Class<?>... argumentTypes) {
        Method method;
        try {
            method = type.getDeclaredMethod(name, argumentTypes);
        } catch (NoSuchMethodException e) {
            throw new CdoException("Cannot resolve method '" + name + "' (" + Arrays.asList(argumentTypes) + ")");
        }
        addProxyMethod(proxyMethod, method);
    }

    private void addProxyMethod(ProxyMethod proxyMethod, Method method) {
        if (method != null) {
            proxyMethods.put(method, proxyMethod);
        }
    }
}
