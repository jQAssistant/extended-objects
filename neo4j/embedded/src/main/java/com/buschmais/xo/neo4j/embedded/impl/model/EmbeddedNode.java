package com.buschmais.xo.neo4j.embedded.impl.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.buschmais.xo.neo4j.api.model.Neo4jNode;

public class EmbeddedNode extends AbstractEmbeddedPropertyContainer<Node>
        implements Neo4jNode<EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType, EmbeddedDirection> {

    private long id;

    private final Set<EmbeddedLabel> labels;

    public EmbeddedNode(Node delegate) {
        super(delegate);
        this.id = delegate.getId();
        this.labels = new HashSet<>();
        for (Label label : delegate.getLabels()) {
            labels.add(new EmbeddedLabel(label));
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public void delete() {
        delegate.delete();
    }

    @Override
    public Iterable<EmbeddedRelationship> getRelationships(EmbeddedRelationshipType type, EmbeddedDirection dir) {
        Iterable<Relationship> relationships = delegate.getRelationships(type.getDelegate(), dir.getDelegate());
        return () -> {
            Iterator<Relationship> iterator = relationships.iterator();
            return new Iterator<EmbeddedRelationship>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public EmbeddedRelationship next() {
                    return new EmbeddedRelationship(iterator.next());
                }
            };
        };

    }

    @Override
    public boolean hasRelationship(EmbeddedRelationshipType type, EmbeddedDirection dir) {
        return delegate.hasRelationship(type.getDelegate(), dir.getDelegate());
    }

    @Override
    public EmbeddedRelationship getSingleRelationship(EmbeddedRelationshipType type, EmbeddedDirection dir) {
        return new EmbeddedRelationship(delegate.getSingleRelationship(type.getDelegate(), dir.getDelegate()));
    }

    public EmbeddedRelationship createRelationshipTo(EmbeddedNode otherNode, EmbeddedRelationshipType type) {
        return new EmbeddedRelationship(delegate.createRelationshipTo(otherNode.getDelegate(), type.getDelegate()));
    }

    public void addLabel(EmbeddedLabel label) {
        delegate.addLabel(label.getDelegate());
        labels.add(label);
    }

    public void removeLabel(EmbeddedLabel label) {
        delegate.removeLabel(label.getDelegate());
        labels.remove(label);
    }

    @Override
    public boolean hasLabel(EmbeddedLabel label) {
        return labels.contains(label);
    }

    @Override
    public Set<EmbeddedLabel> getLabels() {
        return labels;
    }
}
