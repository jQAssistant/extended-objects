package com.buschmais.cdo.impl.interceptor;

public abstract class AbstractCdoInterceptor<T> implements CdoInterceptor<T> {

    private final T delegate;

    public AbstractCdoInterceptor(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public T getDelegate() {
        return delegate;
    }

}
