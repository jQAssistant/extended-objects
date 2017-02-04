package com.buschmais.xo.neo4j.api.model;

/**
 * Defines a relationship.
 * 
 * @param <N>
 *            The type representing nodes.
 * @param <T>
 *            The type representing relationship types.
 */
public interface Neo4jRelationship<N extends Neo4jNode<?, ?, T, ?>, T extends Neo4jRelationshipType> extends Neo4jPropertyContainer {

    /**
     * Return the id of the relationship.
     * 
     * @return The id.
     */
    long getId();

    /**
     * Return the start node of the relationship.
     * 
     * @return The start node.
     */
    N getStartNode();

    /**
     * Return the end node of the relationship.
     * 
     * @return The end node.
     */
    N getEndNode();

    /**
     * Return the type of the relationship.
     * 
     * @return The type.
     */
    T getType();

}
