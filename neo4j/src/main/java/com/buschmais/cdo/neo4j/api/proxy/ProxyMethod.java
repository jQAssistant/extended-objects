package com.buschmais.cdo.neo4j.api.proxy;

import org.neo4j.graphdb.Node;

public interface ProxyMethod<T> {

    Object invoke(T element, Object instance, Object[] args);

}
