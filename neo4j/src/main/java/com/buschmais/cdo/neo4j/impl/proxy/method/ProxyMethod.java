package com.buschmais.cdo.neo4j.impl.proxy.method;

import org.neo4j.graphdb.Node;

public interface ProxyMethod {
    Object invoke(Node node, Object[] args);
}
