package com.buschmais.cdo.impl.proxy.interceptor;

public abstract class AbstractCdoInterceptor<T> implements CdoInterceptor<T> {

    private T delegate;

    public AbstractCdoInterceptor(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public T getDelegate() {
        return delegate;
    }

}
