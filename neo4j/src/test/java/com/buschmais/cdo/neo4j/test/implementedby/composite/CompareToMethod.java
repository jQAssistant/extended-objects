package com.buschmais.cdo.neo4j.test.implementedby.composite;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

public class CompareToMethod implements ProxyMethod<Node> {

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        return ((A) instance).getValue() - ((A) args[0]).getValue();
    }

}
