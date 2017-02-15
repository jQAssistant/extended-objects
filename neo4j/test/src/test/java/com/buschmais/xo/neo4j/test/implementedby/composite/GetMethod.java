package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;

public class GetMethod implements ProxyMethod<Neo4jPropertyContainer> {

    @Override
    public Object invoke(Neo4jPropertyContainer propertyContainer, Object instance, Object[] args) {
        return propertyContainer.getProperty("test") + "_get";
    }
}
