package com.buschmais.xo.impl.proxy.common;

import java.lang.reflect.Method;

import com.buschmais.xo.api.proxy.ProxyMethod;

/**
 * Implementation of delegate method.
 *
 * @param <T>
 *            The proxy type.
 */
public class DelegateMethod<T> implements ProxyMethod<T> {

    private T delegate;

    private Method method;

    /**
     * Constructor.
     * 
     * @param delegate
     *            The delegate instance.
     * @param method
     *            The method to delegate.
     */
    public DelegateMethod(T delegate, Method method) {
        this.delegate = delegate;
        this.method = method;
    }

    @Override
    public Object invoke(T repository, Object instance, Object[] args) throws Exception {
        return method.invoke(delegate, args);
    }
}
