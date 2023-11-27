package com.buschmais.xo.neo4j.embedded.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastoreTransaction;

import org.neo4j.graphdb.Relationship;

public class EmbeddedRelationship extends AbstractEmbeddedPropertyContainer<Relationship>
        implements Neo4jRelationship<EmbeddedNode, EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType, EmbeddedDirection> {

    private final EmbeddedNode startNode;

    private final EmbeddedRelationshipType type;

    private final EmbeddedNode endNode;

    public EmbeddedRelationship(EmbeddedDatastoreTransaction transaction, Relationship relationship) {
        super(transaction, relationship);
        this.startNode = new EmbeddedNode(transaction, relationship.getStartNode());
        this.type = new EmbeddedRelationshipType(relationship.getType());
        this.endNode = new EmbeddedNode(transaction, relationship.getEndNode());
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
        return startNode;
    }

    @Override
    public EmbeddedNode getEndNode() {
        return endNode;
    }

    @Override
    public EmbeddedRelationshipType getType() {
        return type;
    }

}
