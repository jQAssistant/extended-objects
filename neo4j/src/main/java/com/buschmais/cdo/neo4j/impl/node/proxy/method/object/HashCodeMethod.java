package com.buschmais.cdo.neo4j.impl.node.proxy.method.object;

import com.buschmais.cdo.neo4j.api.proxy.NodeProxyMethod;
import org.neo4j.graphdb.Node;

public class HashCodeMethod implements NodeProxyMethod {

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        return Long.valueOf(node.getId());
    }
}