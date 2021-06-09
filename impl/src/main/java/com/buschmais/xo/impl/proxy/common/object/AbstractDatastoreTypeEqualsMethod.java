package com.buschmais.xo.impl.proxy.common.object;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.spi.session.InstanceManager;

public abstract class AbstractDatastoreTypeEqualsMethod<T> implements ProxyMethod<T> {

    public final Object invoke(T datastoreType, Object instance, Object[] args) {
        Object other = args[0];
        if (this == other) {
            return true;
        }
        InstanceManager<?, T> instanceManager = getInstanceManager();
        if (instanceManager.isInstance(other)) {
            T otherDatastoreType = instanceManager.getDatastoreType(other);
            return datastoreType.equals(otherDatastoreType);
        }
        return Boolean.FALSE;
    }

    protected abstract InstanceManager<?, T> getInstanceManager();
}
