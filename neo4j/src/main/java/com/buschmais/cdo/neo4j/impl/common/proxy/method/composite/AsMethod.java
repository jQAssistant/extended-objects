package com.buschmais.cdo.neo4j.impl.common.proxy.method.composite;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

public class AsMethod<T> implements ProxyMethod<T> {

    @Override
    public Object invoke(T element, Object instance, Object[] args) {
        return ((Class<?>) args[0]).cast(instance);
    }
}
