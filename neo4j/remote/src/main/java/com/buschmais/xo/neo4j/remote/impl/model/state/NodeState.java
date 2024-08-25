package com.buschmais.xo.neo4j.remote.impl.model.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteDirection;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;

public class NodeState extends AbstractPropertyContainerState {

    private StateTracker<RemoteLabel, Set<RemoteLabel>> labels = new StateTracker<>(new HashSet<>());
    private Map<RemoteRelationshipType, StateTracker<RemoteRelationship, Set<RemoteRelationship>>> outgoingRelationships = new HashMap<>();
    private Map<RemoteRelationshipType, StateTracker<RemoteRelationship, Set<RemoteRelationship>>> incomingRelationships = new HashMap<>();

    public NodeState() {
        super(null);
    }

    public NodeState(Set<RemoteLabel> labels, Map<String, Object> readCache) {
        super(readCache);
        this.labels = new StateTracker<>(labels);
    }

    public StateTracker<RemoteLabel, Set<RemoteLabel>> getLabels() {
        return labels;
    }

    public Map<RemoteRelationshipType, StateTracker<RemoteRelationship, Set<RemoteRelationship>>> getOutgoingRelationships() {
        return outgoingRelationships;
    }

    public Map<RemoteRelationshipType, StateTracker<RemoteRelationship, Set<RemoteRelationship>>> getIncomingRelationships() {
        return incomingRelationships;
    }

    public StateTracker<RemoteRelationship, Set<RemoteRelationship>> getOutgoingRelationships(RemoteRelationshipType type) {
        return outgoingRelationships.get(type);
    }

    public StateTracker<RemoteRelationship, Set<RemoteRelationship>> getIncomingRelationships(RemoteRelationshipType type) {
        return incomingRelationships.get(type);
    }

    public StateTracker<RemoteRelationship, Set<RemoteRelationship>> getRelationships(RemoteDirection remoteDirection, RemoteRelationshipType type) {
        switch (remoteDirection) {
        case OUTGOING:
            return getOutgoingRelationships(type);
        case INCOMING:
            return getIncomingRelationships(type);
        default:
            throw new XOException("Unknown direction " + remoteDirection);
        }
    }

    public void setRelationships(RemoteDirection remoteDirection, RemoteRelationshipType type,
        StateTracker<RemoteRelationship, Set<RemoteRelationship>> relationships) {
        switch (remoteDirection) {
        case OUTGOING:
            outgoingRelationships.put(type, relationships);
            break;
        case INCOMING:
            incomingRelationships.put(type, relationships);
            break;
        default:
            throw new XOException("Unknown direction " + remoteDirection);
        }
    }

    @Override
    public void flush() {
        super.flush();
        labels.flush();
        for (StateTracker<RemoteRelationship, Set<RemoteRelationship>> tracker : outgoingRelationships.values()) {
            tracker.flush();
        }
        for (StateTracker<RemoteRelationship, Set<RemoteRelationship>> tracker : incomingRelationships.values()) {
            tracker.flush();
        }
    }

    @Override
    public void afterCompletion(boolean clear) {
        super.afterCompletion(clear);
        outgoingRelationships.clear();
        incomingRelationships.clear();
    }
}
