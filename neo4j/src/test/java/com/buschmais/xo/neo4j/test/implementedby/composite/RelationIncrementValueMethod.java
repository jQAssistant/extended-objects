package com.buschmais.xo.neo4j.test.implementedby.composite;

import org.neo4j.graphdb.Relationship;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class RelationIncrementValueMethod implements ProxyMethod<Relationship> {

    @Override
    public Object invoke(Relationship entity, Object instance, Object[] args) {
        A2B a2b = A2B.class.cast(instance);
        int value = a2b.getValue();
        value++;
        a2b.setValue(value);
        return value;
    }

}
