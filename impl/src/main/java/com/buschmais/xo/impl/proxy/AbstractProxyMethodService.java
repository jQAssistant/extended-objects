package com.buschmais.xo.impl.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.proxy.common.UnsupportedOperationMethod;
import com.buschmais.xo.spi.metadata.method.ImplementedByMethodMetadata;
import com.buschmais.xo.spi.metadata.method.MethodMetadata;
import com.buschmais.xo.spi.metadata.method.UnsupportedOperationMethodMetadata;
import com.buschmais.xo.spi.reflection.AnnotatedMethod;

public abstract class AbstractProxyMethodService<E> implements ProxyMethodService<E> {

    private final Map<Method, ProxyMethod<E>> proxyMethods = new HashMap<>();

    @Override
    public Object invoke(E element, Object instance, Method method, Object[] args) throws Exception {
        ProxyMethod<E> proxyMethod = proxyMethods.get(method);
        if (proxyMethod == null) {
            throw new XOException("Cannot find proxy for method '" + method.toGenericString() + "'");
        }
        return proxyMethod.invoke(element, instance, args);
    }

    protected void addMethod(ProxyMethod<E> proxyMethod, Class<?> type, String name, Class<?>... argumentTypes) {
        Method method;
        try {
            method = type.getDeclaredMethod(name, argumentTypes);
        } catch (NoSuchMethodException e) {
            throw new XOException("Cannot resolve method '" + name + "' (" + Arrays.asList(argumentTypes) + ")", e);
        }
        addProxyMethod(proxyMethod, method);
    }

    protected void addProxyMethod(ProxyMethod<E> proxyMethod, Method method) {
        if (method != null) {
            proxyMethods.put(method, proxyMethod);
        }
    }

    protected void addImplementedByMethod(MethodMetadata methodMetadata, AnnotatedMethod typeMethod) {
        if (methodMetadata instanceof ImplementedByMethodMetadata) {
            ImplementedByMethodMetadata implementedByMethodMetadata = (ImplementedByMethodMetadata) methodMetadata;
            Class<? extends ProxyMethod> proxyMethodType = implementedByMethodMetadata.getProxyMethodType();
            try {
                addProxyMethod(proxyMethodType.newInstance(), typeMethod.getAnnotatedElement());
            } catch (InstantiationException e) {
                throw new XOException("Cannot instantiate proxy method of type " + proxyMethodType.getName(), e);
            } catch (IllegalAccessException e) {
                throw new XOException("Unexpected exception while instantiating type " + proxyMethodType.getName(), e);
            }
        }
    }

    protected void addUnsupportedOperationMethod(MethodMetadata methodMetadata, AnnotatedMethod typeMethod) {
        if (methodMetadata instanceof UnsupportedOperationMethodMetadata) {
            addProxyMethod(new UnsupportedOperationMethod((UnsupportedOperationMethodMetadata) methodMetadata), typeMethod.getAnnotatedElement());
        }
    }

}
