package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.model.AbstractNeo4jPropertyContainer;
import org.neo4j.graphdb.PropertyContainer;

public class SetMethod implements ProxyMethod<AbstractNeo4jPropertyContainer> {

    @Override
    public Object invoke(AbstractNeo4jPropertyContainer propertyContainer, Object instance, Object[] args) {
        String value = (String) args[0];
        propertyContainer.setProperty("test", "set_" + value);
        return null;
    }
}
