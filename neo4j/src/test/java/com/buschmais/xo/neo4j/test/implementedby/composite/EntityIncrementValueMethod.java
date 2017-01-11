package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import org.neo4j.graphdb.Node;

public class EntityIncrementValueMethod implements ProxyMethod<Neo4jNode> {

    @Override
    public Object invoke(Neo4jNode entity, Object instance, Object[] args) {
        A a = A.class.cast(instance);
        int value = a.getValue();
        value++;
        a.setValue(value);
        return value;
    }

}
