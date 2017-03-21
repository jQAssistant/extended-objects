package com.buschmais.xo.neo4j.remote.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.state.RelationshipState;

public class RemoteRelationship extends AbstractRemotePropertyContainer<RelationshipState>
        implements Neo4jRelationship<RemoteNode, RemoteLabel, RemoteRelationship, RemoteRelationshipType, RemoteDirection> {

    private RemoteNode startNode;

    private RemoteNode endNode;

    private RemoteRelationshipType relationshipType;

    public RemoteRelationship(long id, RelationshipState state, RemoteNode startNode, RemoteRelationshipType relationshipType, RemoteNode endNode) {
        super(id, state);
        this.startNode = startNode;
        this.relationshipType = relationshipType;
        this.endNode = endNode;
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

}
