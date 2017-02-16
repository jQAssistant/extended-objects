package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.embedded.impl.model.EmbeddedNeo4jPropertyContainer;

public class SetMethod implements ProxyMethod<EmbeddedNeo4jPropertyContainer> {

    @Override
    public Object invoke(EmbeddedNeo4jPropertyContainer propertyContainer, Object instance, Object[] args) {
        String value = (String) args[0];
        propertyContainer.setProperty("test", "set_" + value);
        return null;
    }
}
