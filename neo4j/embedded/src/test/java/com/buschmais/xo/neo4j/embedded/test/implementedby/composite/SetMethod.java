package com.buschmais.xo.neo4j.embedded.test.implementedby.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.embedded.impl.model.AbstractEmbeddedPropertyContainer;

public class SetMethod implements ProxyMethod<AbstractEmbeddedPropertyContainer<?>> {

    @Override
    public Object invoke(AbstractEmbeddedPropertyContainer propertyContainer, Object instance, Object[] args) {
        String value = (String) args[0];
        propertyContainer.setProperty("test", "set_" + value);
        return null;
    }
}
