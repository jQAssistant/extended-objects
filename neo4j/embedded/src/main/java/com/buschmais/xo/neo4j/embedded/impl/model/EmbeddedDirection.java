package com.buschmais.xo.neo4j.embedded.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jDirection;

import org.neo4j.graphdb.Direction;

public enum EmbeddedDirection implements Neo4jDirection {

    INCOMING(Direction.INCOMING), OUTGOING(Direction.OUTGOING);

    private Direction delegate;

    EmbeddedDirection(Direction delegate) {
        this.delegate = delegate;
    }

    public Direction getDelegate() {
        return delegate;
    }

    @Override
    public String getName() {
        return name();
    }
}
