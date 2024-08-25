package com.buschmais.xo.impl.proxy.repository.object;

import com.buschmais.xo.api.proxy.ProxyMethod;

import static java.util.Arrays.asList;

public class ToStringMethod<T> implements ProxyMethod<T> {

    @Override
    public Object invoke(T delegate, Object instance, Object[] args) {
        return asList(instance.getClass()
            .getInterfaces()) + "(" + delegate.toString() + ")";
    }
}
