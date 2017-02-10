package com.buschmais.xo.neo4j.remote.impl.model;

import java.util.HashSet;
import java.util.Set;

import com.buschmais.xo.neo4j.api.model.Neo4jNode;
import com.buschmais.xo.neo4j.remote.api.AbstractRemotePropertyContainer;

public class RemoteNode extends AbstractRemotePropertyContainer implements Neo4jNode<RemoteLabel, RemoteRelationship, RemoteRelationshipType, RemoteDirection> {

    private long id;

    private Set<RemoteLabel> labels = new HashSet<>();

    public RemoteNode(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
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
    public Set<RemoteLabel> getLabels() {
        return labels;
    }

}
