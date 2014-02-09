package com.buschmais.cdo.impl.proxy.common.composite;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.proxy.ProxyMethod;

public abstract class AbstractAsMethod<DatastoreType> implements ProxyMethod<DatastoreType> {

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        Class<?> targetType = (Class<?>) args[0];
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (targetType.isAssignableFrom(type)) {
                return getInstance(datastoreType);
            }
        }
        throw new CdoException(instance + " cannot be cast to " + targetType.getName());
    }

    protected abstract Object getInstance(DatastoreType datastoreType);
}
