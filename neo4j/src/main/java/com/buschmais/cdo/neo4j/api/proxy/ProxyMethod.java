package com.buschmais.cdo.neo4j.api.proxy;

import org.neo4j.graphdb.Node;

public interface ProxyMethod {

    Object invoke(Node node, Object instance, Object[] args);

}
