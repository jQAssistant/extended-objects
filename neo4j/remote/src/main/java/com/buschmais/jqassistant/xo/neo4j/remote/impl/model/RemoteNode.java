package com.buschmais.jqassistant.xo.neo4j.remote.impl.model;

import com.buschmais.jqassistant.xo.neo4j.remote.api.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.api.model.Neo4jNode;

public class RemoteNode extends AbstractRemotePropertyContainer implements Neo4jNode<RemoteLabel, RemoteRelationship, RemoteRelationshipType, RemoteDirection> {

    private long id;

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public Iterable<RemoteRelationship> getRelationships(RemoteRelationshipType type, RemoteDirection dir) {
        return null;
    }

    @Override
    public boolean hasRelationship(RemoteRelationshipType type, RemoteDirection dir) {
        return false;
    }

    @Override
    public RemoteRelationship getSingleRelationship(RemoteRelationshipType type, RemoteDirection dir) {
        return null;
    }

    @Override
    public boolean hasLabel(RemoteLabel label) {
        return false;
    }

    @Override
    public Iterable<RemoteLabel> getLabels() {
        return null;
    }

}
