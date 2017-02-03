package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.Direction;

public enum Neo4jDirection {

    INCOMING(Direction.INCOMING), OUTGOING(Direction.OUTGOING);

    private Direction delegate;

    Neo4jDirection(Direction delegate) {
        this.delegate = delegate;
    }

    public Direction getDelegate() {
        return delegate;
    }
}
