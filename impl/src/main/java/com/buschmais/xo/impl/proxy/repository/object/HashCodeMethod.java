package com.buschmais.xo.impl.proxy.repository.object;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;

public class HashCodeMethod implements ProxyMethod<XOManager> {

    @Override
    public Object invoke(XOManager xoManager, Object instance, Object[] args) {
        return xoManager.hashCode();
    }
}
