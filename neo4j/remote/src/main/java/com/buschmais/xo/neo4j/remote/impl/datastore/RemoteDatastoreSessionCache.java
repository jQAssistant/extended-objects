package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.remote.impl.model.state.NodeState;
import com.buschmais.xo.neo4j.remote.impl.model.state.RelationshipState;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class RemoteDatastoreSessionCache {

    private Cache<Long, RemoteNode> nodeCache = CacheBuilder.newBuilder().weakValues().build();

    private Cache<Long, RemoteRelationship> relationshipCache = CacheBuilder.newBuilder().weakValues().build();

    public RemoteNode getNode(long id, NodeState initialState) {
        try {
            return nodeCache.get(id, () -> new RemoteNode(id, initialState));
        } catch (ExecutionException e) {
            throw new XOException("Cannot fetch node");
        }
    }

    public RemoteRelationship getRelationship(long id, RemoteNode source, RemoteRelationshipType type, RemoteNode target, RelationshipState relationshipState) {
        try {
            return relationshipCache.get(id, () -> new RemoteRelationship(id, relationshipState, source, type, target));
        } catch (ExecutionException e) {
            throw new XOException("Cannot fetch node");
        }
    }

    public RemoteNode getNode(Node node) {
        return getNode(node.id(), getNodeState(node));
    }

    public NodeState getNodeState(Node node) {
        Set<RemoteLabel> labels = new HashSet<>();
        for (String label : node.labels()) {
            labels.add(new RemoteLabel(label));
        }
        return new NodeState(labels, new HashMap<>(node.asMap()));
    }

    public RemoteRelationship getRelationship(Node start, Relationship relationship, Node end) {
        RemoteNode startNode = getNode(start);
        RemoteNode endNode = getNode(end);
        RemoteRelationshipType type = new RemoteRelationshipType(relationship.type());
        return getRelationship(relationship.id(), startNode, type, endNode, getRelationshipState(relationship));
    }

    public RemoteRelationship getRelationship(Relationship relationship) {
        RemoteNode startNode = getNode(relationship.startNodeId(), new NodeState(new HashSet<>(), new HashMap<>()));
        RemoteNode endNode = getNode(relationship.endNodeId(), new NodeState(new HashSet<>(), new HashMap<>()));
        RemoteRelationshipType type = new RemoteRelationshipType(relationship.type());
        RelationshipState relationshipState = getRelationshipState(relationship);
        return getRelationship(relationship.id(), startNode, type, endNode, relationshipState);
    }

    public RelationshipState getRelationshipState(Relationship relationship) {
        return new RelationshipState(new HashMap<>(relationship.asMap()));
    }

}
