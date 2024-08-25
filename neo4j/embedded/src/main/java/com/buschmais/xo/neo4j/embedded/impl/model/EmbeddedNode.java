package com.buschmais.xo.neo4j.embedded.impl.model;

import java.util.Iterator;
import java.util.Set;

import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastoreTransaction;

import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.kernel.impl.core.NodeEntity;

public class EmbeddedNode extends AbstractEmbeddedPropertyContainer<Node>
    implements Neo4jNode<EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType, EmbeddedDirection> {

    private final Set<EmbeddedLabel> labels;

    public EmbeddedNode(EmbeddedDatastoreTransaction transaction, Node node) {
        super(transaction, node);
        this.labels = new UnifiedSet<>();
        for (Label label : node.getLabels()) {
            labels.add(new EmbeddedLabel(label));
        }
    }

    @Override
    public Node getDelegate() {
        return transaction.getTransaction()
            .getNodeById(id);
    }

    public void delete() {
        getDelegate().delete();
    }

    @Override
    public Iterable<EmbeddedRelationship> getRelationships(EmbeddedRelationshipType type, EmbeddedDirection dir) {
        // using NodeEntity instead of Node because return type of getRelationships() in Node interface changed from
        // Iterable to ResourceIterable between Neo4j v4 and v5 breaking bytecode compatibility
        NodeEntity node = (NodeEntity) getDelegate();
        ResourceIterator<Relationship> iterator = node.getRelationships(dir.getDelegate(), type.getDelegate())
            .iterator();
        return () -> new Iterator<>() {

            @Override
            public boolean hasNext() {
                boolean hasNext = iterator.hasNext();
                if (!hasNext) {
                    iterator.close();
                }
                return hasNext;
            }

            @Override
            public EmbeddedRelationship next() {
                return getEmbeddedRelationship(iterator.next());
            }
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
