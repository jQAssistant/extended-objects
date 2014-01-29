package com.buschmais.cdo.impl.proxy.common.composite;

import com.buschmais.cdo.api.proxy.ProxyMethod;

public class GetDelegateMethod<DatastoreType> implements ProxyMethod<DatastoreType> {

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) throws Exception {
        return datastoreType;
    }
}
