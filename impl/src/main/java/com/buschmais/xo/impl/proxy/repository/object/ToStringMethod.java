package com.buschmais.xo.impl.proxy.repository.object;

import java.util.Arrays;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class ToStringMethod<T> implements ProxyMethod<T> {

    @Override
    public Object invoke(T delegate, Object instance, Object[] args) throws Exception {
        return Arrays.asList(instance.getClass().getInterfaces()) + "(" + delegate.toString() + ")";
    }
}
