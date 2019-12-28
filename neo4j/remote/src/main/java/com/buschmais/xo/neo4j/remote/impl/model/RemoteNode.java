package com.buschmais.xo.neo4j.remote.impl.model;

import java.util.Set;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.remote.impl.model.state.NodeState;

public class RemoteNode extends AbstractRemotePropertyContainer<NodeState>
        implements Neo4jNode<RemoteLabel, RemoteRelationship, RemoteRelationshipType, RemoteDirection> {

    public RemoteNode(long id, NodeState initialState) {
        super(id, initialState);
    }

    @Override
    public Iterable<RemoteRelationship> getRelationships(RemoteRelationshipType type, RemoteDirection dir) {
        NodeState state = getState();
        switch (dir) {
        case OUTGOING:
            return state.getOutgoingRelationships(type).getElements();
        case INCOMING:
            return state.getIncomingRelationships(type).getElements();
        }
        throw new XOException("Unsupported direction " + dir);
    }

    @Override
    public boolean hasRelationship(RemoteRelationshipType type, RemoteDirection dir) {
        return getRelationships(type, dir).iterator().hasNext();
    }

    @Override
    public RemoteRelationship getSingleRelationship(RemoteRelationshipType type, RemoteDirection dir) {
        return getRelationships(type, dir).iterator().next();
    }

    @Override
    public boolean hasLabel(RemoteLabel label) {
        return getState().getLabels().getElements().contains(label);
    }

    @Override
    public Set<RemoteLabel> getLabels() {
        return getState().getLabels().getElements();
    }

}
