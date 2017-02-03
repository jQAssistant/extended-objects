package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.Direction;

public enum Neo4jDirection {

    INCOMING(Direction.INCOMING), OUTGOING(Direction.OUTGOING);

    private Direction direction;

    Neo4jDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDelegate() {
        return direction;
    }
}
