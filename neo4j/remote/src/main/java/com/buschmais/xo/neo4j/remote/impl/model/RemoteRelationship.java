package com.buschmais.xo.neo4j.remote.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.remote.api.AbstractRemotePropertyContainer;

public class RemoteRelationship extends AbstractRemotePropertyContainer
        implements Neo4jRelationship<RemoteNode, RemoteLabel, RemoteRelationship, RemoteRelationshipType, RemoteDirection> {

    private RemoteNode startNode;

    private RemoteNode endNode;

    private RemoteRelationshipType relationshipType;

    public RemoteRelationship(long id, RemoteNode startNode, RemoteNode endNode, RemoteRelationshipType relationshipType) {
        super(id);
        this.startNode = startNode;
        this.endNode = endNode;
        this.relationshipType = relationshipType;
    }

    @Override
    public RemoteNode getStartNode() {
        return startNode;
    }

    @Override
    public RemoteNode getEndNode() {
        return endNode;
    }

    @Override
    public RemoteRelationshipType getType() {
        return relationshipType;
    }

    @Override
    public String toString() {
        return "RemoteRelationship{" + "id=" + getId() + ", startNode=" + startNode + ", endNode=" + endNode + ", relationshipType=" + relationshipType + '}';
    }
}
