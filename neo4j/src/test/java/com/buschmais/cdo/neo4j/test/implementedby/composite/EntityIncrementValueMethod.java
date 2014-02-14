package com.buschmais.cdo.neo4j.test.implementedby.composite;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Node;

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
