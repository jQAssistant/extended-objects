package com.buschmais.xo.neo4j.api.model;

public interface Neo4jRelationship<N extends Neo4jNode<?, ?, T, ?>, T extends Neo4jRelationshipType> extends Neo4jPropertyContainer {

    long getId();

    N getStartNode();

    N getEndNode();

    T getType();

}
