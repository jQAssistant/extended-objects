package com.buschmais.xo.neo4j.remote.impl.model.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.neo4j.remote.impl.model.RemoteDirection;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;

public class NodeState extends AbstractPropertyContainerState {

    private Set<RemoteLabel> labels;
    private Map<RemoteDirection, Map<RemoteRelationshipType, Set<RemoteRelationship>>> relationships = new HashMap<>();

    public NodeState(Set<RemoteLabel> labels, Map<String, Object> readCache) {
        super(readCache);
        this.labels = labels;
    }

    public Set<RemoteLabel> getLabels() {
        return labels;
    }

    public void setRelationships(RemoteDirection direction, RemoteRelationshipType type, Set<RemoteRelationship> remoteRelationships) {
        Map<RemoteRelationshipType, Set<RemoteRelationship>> map = getRelationshipsByDirection(direction);
        map.put(type, remoteRelationships);
    }

    public Set<RemoteRelationship> getRelationships(RemoteDirection direction, RemoteRelationshipType type) {
        return getRelationshipsByDirection(direction).get(type);
    }

    private Map<RemoteRelationshipType, Set<RemoteRelationship>> getRelationshipsByDirection(RemoteDirection direction) {
        Map<RemoteRelationshipType, Set<RemoteRelationship>> relationships = this.relationships.get(direction);
        if (relationships == null) {
            relationships = new HashMap<>();
            this.relationships.put(direction, relationships);
        }
        return relationships;
    }

}
