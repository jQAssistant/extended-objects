package com.buschmais.cdo.impl.interceptor;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.TransactionAttribute;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterceptorFactory {

    List<CdoInterceptor> chain;

    public InterceptorFactory(CdoTransaction cdoTransaction, TransactionAttribute transactionAttribute) {
        CdoInterceptor cdoInterceptor = new TransactionInterceptor(cdoTransaction, transactionAttribute);
        this.chain = new ArrayList<>();
        chain.add(cdoInterceptor);
    }

    public <T> T addInterceptor(T instance) {
        Class<?>[] interfaces = instance.getClass().getInterfaces();
        InterceptorInvocationHandler invocationHandler = new InterceptorInvocationHandler(instance, chain);
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), interfaces, invocationHandler);
    }

    public <T> T removeInterceptor(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (!InterceptorInvocationHandler.class.isAssignableFrom(invocationHandler.getClass())) {
            throw new CdoException(invocationHandler + " implementing " + Arrays.asList(invocationHandler.getClass().getInterfaces()) + " is not of expected type " + InterceptorInvocationHandler.class.getName());
        }
        return (T) ((InterceptorInvocationHandler) invocationHandler).getInstance();
    }
}
