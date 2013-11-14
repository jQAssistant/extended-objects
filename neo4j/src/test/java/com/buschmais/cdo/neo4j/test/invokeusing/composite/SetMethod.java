package com.buschmais.cdo.neo4j.test.invokeusing.composite;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

public class SetMethod implements ProxyMethod {

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        String value = (String) args[0];
        node.setProperty("test", "set_" + value);
        return null;
    }
}
