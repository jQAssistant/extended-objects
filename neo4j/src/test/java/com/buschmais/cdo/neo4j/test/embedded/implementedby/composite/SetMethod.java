package com.buschmais.cdo.neo4j.test.embedded.implementedby.composite;

import com.buschmais.cdo.spi.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

public class SetMethod implements ProxyMethod<Node> {

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        String value = (String) args[0];
        entity.setProperty("test", "set_" + value);
        return null;
    }
}
