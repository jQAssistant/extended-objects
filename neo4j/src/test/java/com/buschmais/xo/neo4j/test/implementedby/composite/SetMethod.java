package com.buschmais.xo.neo4j.test.implementedby.composite;

import org.neo4j.graphdb.PropertyContainer;

import com.buschmais.xo.api.proxy.ProxyMethod;

public class SetMethod implements ProxyMethod<PropertyContainer> {

    @Override
    public Object invoke(PropertyContainer propertyContainer, Object instance, Object[] args) {
        String value = (String) args[0];
        propertyContainer.setProperty("test", "set_" + value);
        return null;
    }
}
