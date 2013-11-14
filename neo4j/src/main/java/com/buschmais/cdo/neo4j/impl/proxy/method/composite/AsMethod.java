package com.buschmais.cdo.neo4j.impl.proxy.method.composite;

import com.buschmais.cdo.neo4j.impl.proxy.method.ProxyMethod;
import org.neo4j.graphdb.Node;

public class AsMethod implements ProxyMethod {


    @Override
    public Object invoke(Node node, Object instance, Object[] args) {
        return ((Class<?>) args[0]).cast(instance);
    }
}
