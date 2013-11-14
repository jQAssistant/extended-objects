package com.buschmais.cdo.neo4j.test.invokeusing.composite;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

public class GetMethod implements ProxyMethod{

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        return node.getProperty("test") + "_get";
    }
}
