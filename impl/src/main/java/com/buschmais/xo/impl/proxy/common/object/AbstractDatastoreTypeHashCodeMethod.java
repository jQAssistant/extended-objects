package com.buschmais.xo.impl.proxy.common.object;

import com.buschmais.xo.api.proxy.ProxyMethod;

public abstract class AbstractDatastoreTypeHashCodeMethod<T> implements ProxyMethod<T> {

    @Override
    public final Object invoke(T datastoreType, Object instance, Object[] args) {
        return datastoreType.hashCode();
    }

}
