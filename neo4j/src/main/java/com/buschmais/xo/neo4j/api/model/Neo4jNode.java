package com.buschmais.xo.neo4j.api.model;

public interface Neo4jNode<L extends Neo4jLabel, R extends Neo4jRelationship, T extends Neo4jRelationshipType, D extends Neo4jDirection>
        extends Neo4jPropertyContainer {

    long getId();

    Iterable<R> getRelationships(T type, D dir);

    boolean hasRelationship(T type, D dir);

    R getSingleRelationship(T type, D dir);

    boolean hasLabel(L label);

    Iterable<L> getLabels();
}
