package com.buschmais.cdo.impl.interceptor;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.TransactionAttribute;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class InterceptorFactory {

    private CdoTransaction cdoTransaction;
    private TransactionAttribute transactionAttribute;

    public InterceptorFactory(CdoTransaction cdoTransaction, TransactionAttribute transactionAttribute) {
        this.cdoTransaction = cdoTransaction;
        this.transactionAttribute = transactionAttribute;
    }

    public <T> T addInterceptor(T instance) {
        CdoInterceptor<T> cdoInterceptor = new TransactionInterceptor<T>(instance, cdoTransaction, transactionAttribute);
        Class<?>[] interfaces = instance.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), interfaces, cdoInterceptor);
    }

    public <T> boolean hasInterceptor(T instance) {
        return Proxy.isProxyClass(instance.getClass()) && Proxy.getInvocationHandler(instance) instanceof CdoInterceptor;
    }

    public <T> T removeInterceptor(T instance) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        Class<?> interceptorType = TransactionInterceptor.class;
        if (!interceptorType.isAssignableFrom(invocationHandler.getClass())) {
            throw new CdoException(invocationHandler + " implementing " + Arrays.asList(invocationHandler.getClass().getInterfaces()) + " is not of expected type " + interceptorType.getName());
        }
        return ((CdoInterceptor<T>) invocationHandler).getDelegate();
    }
}
