package com.buschmais.xo.neo4j.embedded.impl.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastoreTransaction;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class EmbeddedNode extends AbstractEmbeddedPropertyContainer<Node>
        implements Neo4jNode<EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType, EmbeddedDirection> {

    private final Set<EmbeddedLabel> labels;

    public EmbeddedNode(EmbeddedDatastoreTransaction transaction, Node node) {
        super(transaction, node);
        this.labels = new HashSet<>();
        for (Label label : getDelegate().getLabels()) {
            labels.add(new EmbeddedLabel(label));
        }
    }

    @Override
    public Node getDelegate() {
        return transaction.getTransaction().getNodeById(id);
    }

    public void delete() {
        getDelegate().delete();
    }

    @Override
    public Iterable<EmbeddedRelationship> getRelationships(EmbeddedRelationshipType type, EmbeddedDirection dir) {
        Iterable<Relationship> relationships = getDelegate().getRelationships(dir.getDelegate(), type.getDelegate());
        return () -> {
            Iterator<Relationship> iterator = relationships.iterator();
            return new Iterator<EmbeddedRelationship>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public EmbeddedRelationship next() {
                    return getEmbeddedRelationship(iterator.next());
                }
            };
        };
    }

    @Override
    public boolean hasRelationship(EmbeddedRelationshipType type, EmbeddedDirection dir) {
        return getDelegate().hasRelationship(dir.getDelegate(), type.getDelegate());
    }

    @Override
    public EmbeddedRelationship getSingleRelationship(EmbeddedRelationshipType type, EmbeddedDirection dir) {
        Relationship relationship = getDelegate().getSingleRelationship(type.getDelegate(), dir.getDelegate());
        return relationship != null ? getEmbeddedRelationship(relationship) : null;
    }


    public EmbeddedRelationship createRelationshipTo(EmbeddedNode otherNode, EmbeddedRelationshipType type) {
        return getEmbeddedRelationship(getDelegate().createRelationshipTo(otherNode.getDelegate(), type.getDelegate()));
    }

    public void addLabel(EmbeddedLabel label) {
        getDelegate().addLabel(label.getDelegate());
        labels.add(label);
    }

    public void removeLabel(EmbeddedLabel label) {
        getDelegate().removeLabel(label.getDelegate());
        labels.remove(label);
    }

    @Override
    public Set<EmbeddedLabel> getLabels() {
        return labels;
    }

    private EmbeddedRelationship getEmbeddedRelationship(Relationship relationship) {
        return new EmbeddedRelationship(transaction, relationship);
    }
}
