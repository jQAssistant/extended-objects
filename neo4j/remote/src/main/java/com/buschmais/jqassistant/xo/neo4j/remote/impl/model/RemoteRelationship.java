package com.buschmais.jqassistant.xo.neo4j.remote.impl.model;

import com.buschmais.jqassistant.xo.neo4j.remote.api.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;

public class RemoteRelationship extends AbstractRemotePropertyContainer
        implements Neo4jRelationship<RemoteNode, RemoteLabel, RemoteRelationshipType, RemoteRelationship, RemoteDirection> {

    private long id;

    private RemoteNode startNode;

    private RemoteNode endNode;

    private RemoteRelationshipType relationshipType;

    public RemoteRelationship(long id, RemoteNode startNode, RemoteNode endNode, RemoteRelationshipType relationshipType) {
        this.id = id;
        this.startNode = startNode;
        this.endNode = endNode;
        this.relationshipType = relationshipType;
    }

    @Override
    public long getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RemoteRelationship that = (RemoteRelationship) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "RemoteRelationship{" + "id=" + id + ", startNode=" + startNode + ", endNode=" + endNode + ", relationshipType=" + relationshipType + '}';
    }
}
