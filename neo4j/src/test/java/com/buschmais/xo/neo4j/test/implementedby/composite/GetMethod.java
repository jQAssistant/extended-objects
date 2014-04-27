package com.buschmais.xo.neo4j.test.implementedby.composite;

import org.neo4j.graphdb.PropertyContainer;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class GetMethod implements ProxyMethod<PropertyContainer> {

    @Override
    public Object invoke(PropertyContainer propertyContainer, Object instance, Object[] args) {
        return propertyContainer.getProperty("test") + "_get";
    }
}
