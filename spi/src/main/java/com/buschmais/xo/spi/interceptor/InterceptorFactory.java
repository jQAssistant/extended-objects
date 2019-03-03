package com.buschmais.xo.spi.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.buschmais.xo.api.XOException;

public class InterceptorFactory {

    private final XOInterceptor[] chain;

    public InterceptorFactory(List<? extends XOInterceptor> chain) {
        List<XOInterceptor> effectiveChain = new ArrayList<>(chain.size());
        for (XOInterceptor xoInterceptor : chain) {
            if (xoInterceptor.isActive()) {
                effectiveChain.add(xoInterceptor);
            }
        }
        this.chain = effectiveChain.toArray(new XOInterceptor[effectiveChain.size()]);
    }

    public <T> T addInterceptor(T instance, Class<?>... interfaces) {
        if (chain.length == 0) {
            return instance;
        }
        InterceptorInvocationHandler invocationHandler = new InterceptorInvocationHandler(instance, chain);
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), interfaces, invocationHandler);
    }

    public <T> boolean hasInterceptor(T instance) {
        return Proxy.isProxyClass(instance.getClass()) && Proxy.getInvocationHandler(instance) instanceof InterceptorInvocationHandler;
    }

    public <T> T removeInterceptor(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!InterceptorInvocationHandler.class.isAssignableFrom(invocationHandler.getClass())) {
            throw new XOException(invocationHandler + " implementing " + Arrays.asList(invocationHandler.getClass().getInterfaces())
                    + " is not of expected type " + InterceptorInvocationHandler.class.getName());
        }
        return (T) ((InterceptorInvocationHandler) invocationHandler).getInstance();
    }
}
