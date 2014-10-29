package com.buschmais.xo.impl.proxy.repository.object;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;

public class HashCodeMethod<T> implements ProxyMethod<T> {

    @Override
    public Object invoke(T delegate, Object instance, Object[] args) {
        return delegate.hashCode();
    }
}
