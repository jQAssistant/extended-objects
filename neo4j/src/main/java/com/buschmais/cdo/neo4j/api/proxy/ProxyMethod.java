package com.buschmais.cdo.neo4j.api.proxy;

public interface ProxyMethod<Entity> {

    Object invoke(Entity entity, Object instance, Object[] args) throws Exception;

}
