package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.*;

public class Neo4jNode extends AbstractNeo4jPropertyContainer<Node> {

    public Neo4jNode(Node delegate) {
        super(delegate);
    }

    public long getId() {
        return delegate.getId();
    }

    public void delete() {
        delegate.delete();
    }

    public Iterable<Relationship> getRelationships(RelationshipType type, Direction dir) {
        return delegate.getRelationships(type, dir);
    }

    public boolean hasRelationship(RelationshipType type, Direction dir) {
        return delegate.hasRelationship(type, dir);
    }

    public Relationship getSingleRelationship(RelationshipType type, Direction dir) {
        return delegate.getSingleRelationship(type, dir);
    }

    public Relationship createRelationshipTo(Node otherNode, RelationshipType type) {
        return delegate.createRelationshipTo(otherNode, type);
    }

    public void addLabel(Label label) {
        delegate.addLabel(label);
    }

    public void removeLabel(Label label) {
        delegate.removeLabel(label);
    }

    public boolean hasLabel(Label label) {
        return delegate.hasLabel(label);
    }

    public Iterable<Label> getLabels() {
        return delegate.getLabels();
    }

}
