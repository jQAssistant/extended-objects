package com.buschmais.xo.neo4j.api.model;

/**
 * Defines the interface for a relationship type.
 */
public interface Neo4jRelationshipType {

    /**
     * Return the name of the relationship type.
     * 
     * @return The name.
     */
    String getName();
}
