package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class Neo4jRelationship extends AbstractNeo4jPropertyContainer<Relationship> {

    public Neo4jRelationship(Relationship delegate) {
        super(delegate);
    }

    public long getId() {
        return delegate.getId();
    }

    public void delete() {
        delegate.delete();
    }

    public Node getStartNode() {
        return delegate.getStartNode();
    }

    public Node getEndNode() {
        return delegate.getEndNode();
    }

    public RelationshipType getType() {
        return delegate.getType();
    }

}
