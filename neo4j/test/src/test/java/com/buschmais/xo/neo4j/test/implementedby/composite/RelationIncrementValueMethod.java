package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;

public class RelationIncrementValueMethod implements ProxyMethod<Neo4jRelationship> {

    @Override
    public Object invoke(Neo4jRelationship entity, Object instance, Object[] args) {
        A2B a2b = A2B.class.cast(instance);
        int value = a2b.getValue();
        value++;
        a2b.setValue(value);
        return value;
    }

}
