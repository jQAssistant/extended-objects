package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.api.proxy.ProxyMethod;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import org.neo4j.graphdb.Node;

public class CompareToMethod implements ProxyMethod<Neo4jNode> {

    @Override
    public Object invoke(Neo4jNode node, Object instance, Object[] args) {
        return ((A) instance).getValue() - ((A) args[0]).getValue();
    }

}
