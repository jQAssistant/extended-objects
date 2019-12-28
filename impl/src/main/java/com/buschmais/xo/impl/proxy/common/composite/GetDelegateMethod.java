package com.buschmais.xo.impl.proxy.common.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class GetDelegateMethod<DatastoreType> implements ProxyMethod<DatastoreType> {

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        return datastoreType;
    }
}
