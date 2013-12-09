package com.buschmais.cdo.api.proxy;

public interface ProxyMethod<Entity> {

    Object invoke(Entity entity, Object instance, Object[] args) throws Exception;

}
