package com.buschmais.xo.neo4j.embedded.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastoreTransaction;

import org.neo4j.graphdb.Relationship;

public class EmbeddedRelationship extends AbstractEmbeddedPropertyContainer<Relationship>
        implements Neo4jRelationship<EmbeddedNode, EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType, EmbeddedDirection> {

    public EmbeddedRelationship(EmbeddedDatastoreTransaction transaction, Relationship relationship) {
        super(transaction, relationship);
    }

    @Override
    public Relationship getDelegate() {
        return transaction.getTransaction().getRelationshipById(id);
    }

    public void delete() {
        getDelegate().delete();
    }

    @Override
    public EmbeddedNode getStartNode() {
        return getEmbeddedNode();
    }

    private EmbeddedNode getEmbeddedNode() {
        return new EmbeddedNode(transaction, getDelegate().getStartNode());
    }

    @Override
    public EmbeddedNode getEndNode() {
        return new EmbeddedNode(transaction, getDelegate().getEndNode());
    }

    @Override
    public EmbeddedRelationshipType getType() {
        return new EmbeddedRelationshipType(getDelegate().getType());
    }

}
