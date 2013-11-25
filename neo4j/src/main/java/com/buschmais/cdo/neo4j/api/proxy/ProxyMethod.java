package com.buschmais.cdo.neo4j.api.proxy;

public interface ProxyMethod<T> {

    Object invoke(T element, Object instance, Object[] args);

}
