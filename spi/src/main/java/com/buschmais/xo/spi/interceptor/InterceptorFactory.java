package com.buschmais.xo.spi.interceptor;

import com.buschmais.xo.api.XOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InterceptorFactory {

    private final List<? extends XOInterceptor> chain;

    public InterceptorFactory(List<? extends XOInterceptor> chain) {
        this.chain = chain;
    }

    public <T> T addInterceptor(T instance) {
        return addInterceptor(instance, instance.getClass().getInterfaces());
    }

    public <T> T addInterceptor(T instance, Class<?>... interfaces) {
        InterceptorInvocationHandler invocationHandler = new InterceptorInvocationHandler(instance, chain);
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), interfaces, invocationHandler);
    }

    public <T> boolean hasInterceptor(T instance) {
        return Proxy.isProxyClass(instance.getClass()) && Proxy.getInvocationHandler(instance) instanceof InterceptorInvocationHandler;
    }

    public <T> T removeInterceptor(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!InterceptorInvocationHandler.class.isAssignableFrom(invocationHandler.getClass())) {
            throw new XOException(invocationHandler + " implementing " + Arrays.asList(invocationHandler.getClass().getInterfaces()) + " is not of expected type " + InterceptorInvocationHandler.class.getName());
        }
        return (T) ((InterceptorInvocationHandler) invocationHandler).getInstance();
    }
}
