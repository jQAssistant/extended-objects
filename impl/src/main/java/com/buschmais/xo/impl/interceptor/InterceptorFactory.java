package com.buschmais.xo.impl.interceptor;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.Transaction;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOTransaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterceptorFactory {

    private final List<XOInterceptor> chain;

    public InterceptorFactory(XOTransaction XOTransaction, Transaction.TransactionAttribute transactionAttribute, ConcurrencyMode concurrencyMode) {
        this.chain = new ArrayList<>();
        chain.add(new ConcurrencyInterceptor(concurrencyMode));
        chain.add(new TransactionInterceptor(XOTransaction, transactionAttribute));
    }

    public <T> T addInterceptor(T instance) {
        Class<?>[] interfaces = instance.getClass().getInterfaces();
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
