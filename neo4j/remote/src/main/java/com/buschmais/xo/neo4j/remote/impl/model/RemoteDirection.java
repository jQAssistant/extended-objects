package com.buschmais.xo.neo4j.remote.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jDirection;

public enum RemoteDirection implements Neo4jDirection {

    INCOMING,
    OUTGOING;

    @Override
    public String getName() {
        return name();
    }
}
