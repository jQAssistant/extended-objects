package com.buschmais.xo.neo4j.api.model;

/**
 * Defines a node.
 * 
 * @param <L>
 *            The type representing labels.
 * @param <R>
 *            The type representing relationships.
 * @param <T>
 *            The type representing relationship types.
 * @param <D>
 *            The type representing relationship directions
 */
public interface Neo4jNode<L extends Neo4jLabel, R extends Neo4jRelationship, T extends Neo4jRelationshipType, D extends Neo4jDirection>
        extends Neo4jPropertyContainer {

    /**
     * Return all relationships of the given type and direction.
     * 
     * @param type
     *            The relationship type.
     * @param dir
     *            The direction.
     * @return The relationships.
     */
    Iterable<R> getRelationships(T type, D dir);

    /**
     * Return if the node has a relationship of the given type and direction.
     * 
     * @param type
     *            The relationship type.
     * @param dir
     *            The direction.
     * @return <code>true</code> if the node has at least one relationship.
     */
    boolean hasRelationship(T type, D dir);

    /**
     * Return a single relationship of the given type and direction.
     *
     * @param type
     *            The relationship type.
     * @param dir
     *            The direction.
     * @return The relationship or <code>null</code> if no such relationship exists.
     */
    R getSingleRelationship(T type, D dir);

    /**
     * Return if the node has a label.
     * 
     * @param label
     *            The label.
     * @return <code>true</code> if the node has the label.
     */
    boolean hasLabel(L label);

    /**
     * Return all labels of the node.
     * 
     * @return The labels of the node.
     */
    Iterable<L> getLabels();
}
