package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.*;

import java.util.Iterator;

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

    public void addLabel(Neo4jLabel label) {
        delegate.addLabel(label.getLabel());
    }

    public void removeLabel(Neo4jLabel label) {
        delegate.removeLabel(label.getLabel());
    }

    public boolean hasLabel(Neo4jLabel label) {
        return delegate.hasLabel(label.getLabel());
    }

    public Iterable<Neo4jLabel> getLabels() {
        return () -> {
            Iterator<Label> iterator= delegate.getLabels().iterator();
            return new Iterator<Neo4jLabel>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Neo4jLabel next() {
                    return new Neo4jLabel(iterator.next());
                }
            };
        };
    }
}
