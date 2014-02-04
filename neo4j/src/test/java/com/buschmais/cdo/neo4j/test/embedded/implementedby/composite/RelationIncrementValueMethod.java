package com.buschmais.cdo.neo4j.test.embedded.implementedby.composite;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import org.neo4j.graphdb.Relationship;

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
