package com.buschmais.xo.impl.proxy.repository.object;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.impl.AbstractInstanceManager;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;

public class EqualsMethod implements ProxyMethod<XOManager> {

    @Override
    public Object invoke(XOManager xoManager, Object instance, Object[] args) {
        return instance == args[0];
    }
}
