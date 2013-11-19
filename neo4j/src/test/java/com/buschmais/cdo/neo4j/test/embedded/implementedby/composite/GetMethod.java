package com.buschmais.cdo.neo4j.test.embedded.implementedby.composite;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import org.neo4j.graphdb.Node;

public class GetMethod implements NodeProxyMethod {

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        return node.getProperty("test") + "_get";
    }
}
