package com.buschmais.xo.neo4j.remote.impl.datastore;

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

    public RemoteNode getNode(long id) {
        return getNode(id, new NodeState());
    }

    public RemoteNode getNode(long id, NodeState nodeState) {
        try {
            return nodeCache.get(id, () -> new RemoteNode(id, nodeState));
        } catch (ExecutionException e) {
            throw new XOException("Cannot fetch node");
        }
    }

    public RemoteRelationship getRelationship(long id, RemoteNode source, RemoteRelationshipType type, RemoteNode target) {
        return getRelationship(id, source, type, target, new RelationshipState());
    }

    public RemoteRelationship getRelationship(long id, RemoteNode source, RemoteRelationshipType type, RemoteNode target, RelationshipState relationshipState) {
        try {
            RemoteRelationship remoteRelationship = relationshipCache.get(id, () -> new RemoteRelationship(id, relationshipState, source, type, target));
            return remoteRelationship;
        } catch (ExecutionException e) {
            throw new XOException("Cannot fetch node");
        }
    }

    public RemoteNode getNode(Node node) {
        RemoteNode remoteNode = getNode(node.id());
        NodeState nodeState = remoteNode.getState();
        if (!nodeState.isLoaded()) {
            nodeState.load(node.asMap());
            Set<RemoteLabel> labels = new HashSet<>();
            for (String label : node.labels()) {
                labels.add(new RemoteLabel(label));
            }
            nodeState.getLabels().load(labels);
        }
        return remoteNode;
    }

    public RemoteRelationship getRelationship(Node start, Relationship relationship, Node end) {
        RemoteNode startNode = getNode(start);
        RemoteNode endNode = getNode(end);
        RemoteRelationshipType type = new RemoteRelationshipType(relationship.type());
        return getRelationship(relationship, startNode, endNode, type);
    }

    public RemoteRelationship getRelationship(Relationship relationship) {
        RemoteNode startNode = getNode(relationship.startNodeId());
        RemoteNode endNode = getNode(relationship.endNodeId());
        RemoteRelationshipType type = new RemoteRelationshipType(relationship.type());
        return getRelationship(relationship, startNode, endNode, type);
    }

    private RemoteRelationship getRelationship(Relationship relationship, RemoteNode startNode, RemoteNode endNode, RemoteRelationshipType type) {
        RemoteRelationship remoteRelationship = getRelationship(relationship.id(), startNode, type, endNode);
        if (!remoteRelationship.getState().isLoaded()) {
            remoteRelationship.getState().load(relationship.asMap());
        }
        return remoteRelationship;
    }
}
