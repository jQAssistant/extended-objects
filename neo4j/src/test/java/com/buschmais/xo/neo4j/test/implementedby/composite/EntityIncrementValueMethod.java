package com.buschmais.xo.neo4j.test.implementedby.composite;

import org.neo4j.graphdb.Node;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class EntityIncrementValueMethod implements ProxyMethod<Node> {

    @Override
    public Object invoke(Node entity, Object instance, Object[] args) {
        A a = A.class.cast(instance);
        int value = a.getValue();
        value++;
        a.setValue(value);
        return value;
    }

}
