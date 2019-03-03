package com.buschmais.xo.impl.proxy.repository.object;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class HashCodeMethod<T> implements ProxyMethod<T> {

    @Override
    public Object invoke(T delegate, Object instance, Object[] args) {
        return delegate.hashCode();
    }
}
