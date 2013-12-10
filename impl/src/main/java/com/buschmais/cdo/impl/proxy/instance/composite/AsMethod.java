package com.buschmais.cdo.impl.proxy.instance.composite;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.proxy.ProxyMethod;

public class AsMethod<Entity> implements ProxyMethod<Entity> {

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Class<?> targetType = (Class<?>) args[0];
        for (Class<?> type : instance.getClass().getInterfaces()) {
            if (targetType.isAssignableFrom(type)) {
                return targetType.cast(instance);
            }
        }
        throw new CdoException(instance + " cannot be cast to " + targetType.getName());
    }
}
