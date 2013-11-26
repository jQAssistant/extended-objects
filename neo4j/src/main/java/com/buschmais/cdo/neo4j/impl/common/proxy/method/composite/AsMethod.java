package com.buschmais.cdo.neo4j.impl.common.proxy.method.composite;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

public class AsMethod<Entity> implements ProxyMethod<Entity> {

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return ((Class<?>) args[0]).cast(instance);
    }
}
