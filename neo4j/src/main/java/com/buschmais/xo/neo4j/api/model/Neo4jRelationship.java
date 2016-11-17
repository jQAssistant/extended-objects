package com.buschmais.xo.neo4j.api.model;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class Neo4jRelationship extends AbstractNeo4jPropertyContainer<Relationship> implements Relationship {

    public Neo4jRelationship(Relationship delegate) {
        super(delegate);
    }

    @Override
    public long getId() {
        return delegate.getId();
    }

    @Override
    public void delete() {
        delegate.delete();
    }

    @Override
    public Node getStartNode() {
        return delegate.getStartNode();
    }

    @Override
    public Node getEndNode() {
        return delegate.getEndNode();
    }

    @Override
    public Node getOtherNode(Node node) {
        return delegate.getOtherNode(node);
    }

    @Override
    public Node[] getNodes() {
        return delegate.getNodes();
    }

    @Override
    public RelationshipType getType() {
        return delegate.getType();
    }

    @Override
    public boolean isType(RelationshipType type) {
        return delegate.isType(type);
    }
}
