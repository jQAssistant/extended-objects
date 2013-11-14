package com.buschmais.cdo.neo4j.test.invokeusing.composite;

import com.buschmais.cdo.neo4j.impl.proxy.method.ProxyMethod;
import org.neo4j.graphdb.Node;

public class IncrementValueMethod implements ProxyMethod {

    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        A a = A.class.cast(instance);
        int value = a.getValue();
        value++;
        a.setValue(value);
        return value;
    }

}
