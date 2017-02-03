package com.buschmais.xo.neo4j.api.model;

import java.util.Iterator;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

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

    public Iterable<Neo4jRelationship> getRelationships(Neo4jRelationshipType type, Neo4jDirection dir) {
        Iterable<Relationship> relationships = delegate.getRelationships(type.getRelationshipType(), dir.getDelegate());
        return () -> {
            Iterator<Relationship> iterator = relationships.iterator();
            return new Iterator<Neo4jRelationship>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Neo4jRelationship next() {
                    return new Neo4jRelationship(iterator.next());
                }
            };
        };

    }

    public boolean hasRelationship(Neo4jRelationshipType type, Neo4jDirection dir) {
        return delegate.hasRelationship(type.getRelationshipType(), dir.getDelegate());
    }

    public Neo4jRelationship getSingleRelationship(Neo4jRelationshipType type, Neo4jDirection dir) {
        return new Neo4jRelationship(delegate.getSingleRelationship(type.getRelationshipType(), dir.getDelegate()));
    }

    public Neo4jRelationship createRelationshipTo(Neo4jNode otherNode, Neo4jRelationshipType type) {
        return new Neo4jRelationship(delegate.createRelationshipTo(otherNode.getDelegate(), type.getRelationshipType()));
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
            Iterator<Label> iterator = delegate.getLabels().iterator();
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
