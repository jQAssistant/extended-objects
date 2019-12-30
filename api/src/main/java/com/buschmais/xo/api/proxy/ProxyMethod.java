package com.buschmais.xo.api.proxy;

public interface ProxyMethod<Entity> {

    @SuppressWarnings("squid:S00112")
    Object invoke(Entity entity, Object instance, Object[] args) throws Exception;

}
