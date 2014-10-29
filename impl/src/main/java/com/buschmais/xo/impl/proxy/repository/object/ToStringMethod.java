package com.buschmais.xo.impl.proxy.repository.object;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.common.object.AbstractToStringMethod;

import java.util.Arrays;

public class ToStringMethod<T> implements ProxyMethod<T> {

    @Override
    public Object invoke(T delegate, Object instance, Object[] args) throws Exception {
        return Arrays.asList(instance.getClass().getInterfaces()) + "(" + delegate.toString() + ")";
    }
}
