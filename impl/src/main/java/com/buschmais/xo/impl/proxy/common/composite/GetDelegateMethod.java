package com.buschmais.xo.impl.proxy.common.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.SessionContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetDelegateMethod<Entity, Relation, DatastoreType> implements ProxyMethod<DatastoreType> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        return datastoreType;
    }
}
