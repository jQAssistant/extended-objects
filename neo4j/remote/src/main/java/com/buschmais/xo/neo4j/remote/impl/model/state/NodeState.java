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
    private Map<RemoteDirection, Map<RemoteRelationshipType, Set<RemoteRelationship>>> cachedRelationships = new HashMap<>();

    public NodeState(Set<RemoteLabel> labels, Map<String, Object> readCache) {
        super(readCache);
        this.labels = labels;
    }

    public Set<RemoteLabel> getLabels() {
        return labels;
    }

    public Set<RemoteRelationship> getRelationships(RemoteDirection direction, RemoteRelationshipType type) {
        Map<RemoteRelationshipType, Set<RemoteRelationship>> relationshipsByDirection = getRelationshipsByDirection(direction);
        return relationshipsByDirection.get(type);
    }

    public void setRelationships(RemoteDirection direction, RemoteRelationshipType type, Set<RemoteRelationship> relationships) {
        Map<RemoteRelationshipType, Set<RemoteRelationship>> relationshipsByDirection = getRelationshipsByDirection(direction);
        relationshipsByDirection.put(type, relationships);
    }

    private Map<RemoteRelationshipType, Set<RemoteRelationship>> getRelationshipsByDirection(RemoteDirection direction) {
        Map<RemoteRelationshipType, Set<RemoteRelationship>> relationships = this.cachedRelationships.get(direction);
        if (relationships == null) {
            relationships = new HashMap<>();
            this.cachedRelationships.put(direction, relationships);
        }
        return relationships;
    }

}
