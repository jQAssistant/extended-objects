package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class Neo4jRelationship extends AbstractNeo4jPropertyContainer<Relationship> {

    private Neo4jNode startNode;

    private Neo4jNode endNode;

    public Neo4jRelationship(Relationship delegate) {
        super(delegate);
        this.startNode = new Neo4jNode(delegate.getStartNode());
        this.endNode = new Neo4jNode(delegate.getEndNode());
    }

    public long getId() {
        return delegate.getId();
    }

    public void delete() {
        delegate.delete();
    }

    public Neo4jNode getStartNode() {
        return startNode;
    }

    public Neo4jNode getEndNode() {
        return endNode;
    }

    public RelationshipType getType() {
        return delegate.getType();
    }

}
