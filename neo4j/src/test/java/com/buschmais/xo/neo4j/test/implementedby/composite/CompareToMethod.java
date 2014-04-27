package com.buschmais.xo.neo4j.test.implementedby.composite;

import org.neo4j.graphdb.Node;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class CompareToMethod implements ProxyMethod<Node> {

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        return ((A) instance).getValue() - ((A) args[0]).getValue();
    }

}
