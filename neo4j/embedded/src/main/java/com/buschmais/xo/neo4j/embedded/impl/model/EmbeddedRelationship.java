package com.buschmais.xo.neo4j.embedded.impl.model;

import org.neo4j.graphdb.Relationship;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;

public class EmbeddedRelationship extends AbstractEmbeddedPropertyContainer<Relationship>
        implements Neo4jRelationship<EmbeddedNode, EmbeddedLabel, EmbeddedRelationship, EmbeddedRelationshipType, EmbeddedDirection> {

    private EmbeddedNode startNode;

    private EmbeddedNode endNode;

    public EmbeddedRelationship(Relationship delegate) {
        super(delegate.getId(), delegate);
        this.startNode = new EmbeddedNode(delegate.getStartNode());
        this.endNode = new EmbeddedNode(delegate.getEndNode());
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
