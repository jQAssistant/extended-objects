package com.buschmais.cdo.impl.proxy.interceptor;

import com.buschmais.cdo.api.CdoException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class InterceptorFactory {

    private InterceptorFactory() {
    }

    public static <T> T addInterceptor(T instance, CdoInterceptor interceptor) {
        Class<?>[] interfaces = instance.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), interfaces, interceptor);
    }

    public static <T> T removeInterceptor(T instance, Class<? extends CdoInterceptor> interceptorType) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!interceptorType.isAssignableFrom(invocationHandler.getClass())) {
            throw new CdoException(invocationHandler + " implementing " + Arrays.asList(invocationHandler.getClass().getInterfaces()) + " is not of expected type " + interceptorType.getName());
        }
        return ((CdoInterceptor<T>) invocationHandler).getDelegate();
    }
}
