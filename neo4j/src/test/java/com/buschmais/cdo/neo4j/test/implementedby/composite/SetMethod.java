package com.buschmais.cdo.neo4j.test.implementedby.composite;

import com.buschmais.cdo.api.proxy.ProxyMethod;
import org.neo4j.graphdb.PropertyContainer;

public class SetMethod implements ProxyMethod<PropertyContainer> {

    @Override
    public Object invoke(PropertyContainer propertyContainer, Object instance, Object[] args) {
        String value = (String) args[0];
        propertyContainer.setProperty("test", "set_" + value);
        return null;
    }
}
