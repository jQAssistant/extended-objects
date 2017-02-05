package com.buschmais.xo.neo4j.embedded.impl.model;

import org.neo4j.graphdb.Relationship;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;

public class EmbeddedRelationship extends AbstractEmbeddedPropertyContainer<Relationship> implements Neo4jRelationship<EmbeddedNode, EmbeddedRelationshipType> {

    private EmbeddedNode startNode;

    private EmbeddedNode endNode;

    public EmbeddedRelationship(Relationship delegate) {
        super(delegate);
        this.startNode = new EmbeddedNode(delegate.getStartNode());
        this.endNode = new EmbeddedNode(delegate.getEndNode());
    }

    @Override
    public long getId() {
        return delegate.getId();
    }

    public void delete() {
        delegate.delete();
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
        return new EmbeddedRelationshipType(delegate.getType());
    }

}