package com.buschmais.cdo.impl.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class InterceptorInvocationHandler implements InvocationHandler {

    private final Object instance;
    private final List<CdoInterceptor> chain;

    public InterceptorInvocationHandler(Object instance, List<CdoInterceptor> chain) {
        this.instance = instance;
        this.chain = chain;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvocationContext invocationContext = new InvocationContext(instance, method, args, chain);
        return invocationContext.proceed();
    }

    public Object getInstance() {
        return instance;
    }
}
