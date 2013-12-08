package com.buschmais.cdo.impl.proxy.instance.composite;

import com.buschmais.cdo.spi.proxy.ProxyMethod;

public class AsMethod<Entity> implements ProxyMethod<Entity> {

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return ((Class<?>) args[0]).cast(instance);
    }
}
