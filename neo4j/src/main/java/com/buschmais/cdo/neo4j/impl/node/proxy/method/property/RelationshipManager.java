package com.buschmais.cdo.neo4j.impl.node.proxy.method.property;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.node.metadata.AbstractRelationshipPropertyMethodMetadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.Iterator;

/**
 * Contains methods for reading and creating relationships specified by the given metadata.
 * <p/>
 * <p>For each provided method the direction of the relationships is handled transparently for the caller.</p>
 */
public class RelationshipManager {

    private RelationshipType relationshipType;
    private Direction direction;

    /**
     * Constructor.
     *
     * @param metadata The relation ship metadata.
     */
    public RelationshipManager(AbstractRelationshipPropertyMethodMetadata metadata) {
        relationshipType = metadata.getRelationshipType();
        direction = metadata.getDirection();
    }

    /**
     * Create a single relationship from a node to another.
     * <p>An existing relationship will be discarded.</p>
     *
     * @param node   The node.
     * @param target The target node or <code>null</code>.
     */
    public void createSingleRelationship(Node node, Node target) {
        if (node.hasRelationship(relationshipType, direction)) {
            Relationship relationship = node.getSingleRelationship(relationshipType, direction);
            relationship.delete();
        }
        if (target != null) {
            switch (direction) {
                case OUTGOING:
                    node.createRelationshipTo(target, relationshipType);
                    break;
                case INCOMING:
                    target.createRelationshipTo(node, relationshipType);
                    break;
                default:
                    throw new CdoException("Unsupported direction: " + direction);
            }
        }
    }

    /**
     * Get the target node of a single relationship.
     *
     * @param node The node.
     * @return The target node or <code>null</code>.
     */
    public Node getSingleRelationship(Node node) {
        Relationship relationship = node.getSingleRelationship(relationshipType, direction);
        return relationship != null ? getTargetNode(relationship) : null;
    }

    /**
     * Remove an existing relationship.
     *
     * @param node   The node.
     * @param target The target node.
     * @return <code>true</code> if an existing relationship has been removed.
     */
    public boolean removeRelationship(Node node, Node target) {
        for (Relationship relationship : node.getRelationships(relationshipType, direction)) {
            if (getTargetNode(relationship).equals(target)) {
                relationship.delete();
                return true;
            }
        }
        return false;
    }

    /**
     * Remove all relationships of a node.
     *
     * @param node The node.
     */
    public void removeRelationships(Node node) {
        for (Relationship relationship : node.getRelationships(relationshipType, direction)) {
            relationship.delete();
        }
    }

    /**
     * Create a relationship to a node.
     *
     * @param node   The node.
     * @param target The target node.
     */
    public void createRelationship(Node node, Node target) {
        switch (direction) {
            case OUTGOING:
                node.createRelationshipTo(target, relationshipType);
                break;
            case INCOMING:
                target.createRelationshipTo(node, relationshipType);
                break;
            default:
                throw new CdoException("Unsupported direction: " + direction);
        }
    }

    /**
     * Return all relationships of a node.
     *
     * @param node The node.
     * @return An iterator delivering all target nodes.
     */
    public Iterator<Node> getRelationships(Node node) {
        Iterable<Relationship> relationships = node.getRelationships(relationshipType, direction);
        final Iterator<Relationship> iterator = relationships.iterator();
        return new Iterator<Node>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Node next() {
                Relationship next = iterator.next();
                return getTargetNode(next);
            }

            @Override
            public void remove() {
            }
        };
    }

    private Node getTargetNode(Relationship relationship) {
        switch (direction) {
            case OUTGOING:
                return relationship.getEndNode();
            case INCOMING:
                return relationship.getStartNode();
            default:
                throw new CdoException("Unsupported direction: " + direction);
        }
    }
}
