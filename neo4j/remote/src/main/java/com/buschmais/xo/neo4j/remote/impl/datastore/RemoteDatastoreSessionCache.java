package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.buschmais.xo.neo4j.remote.impl.model.*;
import com.buschmais.xo.neo4j.remote.impl.model.state.AbstractPropertyContainerState;
import com.buschmais.xo.neo4j.remote.impl.model.state.NodeState;
import com.buschmais.xo.neo4j.remote.impl.model.state.RelationshipState;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

public class RemoteDatastoreSessionCache {

    private Cache<Long, RemoteNode> nodeCache = Caffeine.newBuilder()
        .weakValues()
        .build();

    private Cache<Long, RemoteRelationship> relationshipCache = Caffeine.newBuilder()
        .weakValues()
        .build();

    public RemoteRelationship getRelationship(Long id) {
        return relationshipCache.getIfPresent(id);
    }

    public RemoteNode getNode(long id) {
        return getNode(id, () -> new NodeState());
    }

    public RemoteNode getNode(long id, Supplier<NodeState> nodeStateSupplier) {
        return nodeCache.get(id, key -> new RemoteNode(key, nodeStateSupplier.get()));
    }

    public RemoteRelationship getRelationship(long id, RemoteNode source, RemoteRelationshipType type, RemoteNode target) {
        return getRelationship(id, source, type, target, () -> new RelationshipState());
    }

    public RemoteRelationship getRelationship(long id, RemoteNode source, RemoteRelationshipType type, RemoteNode target,
        Supplier<RelationshipState> relationshipStateSupplier) {
        return relationshipCache.get(id, key -> new RemoteRelationship(key, relationshipStateSupplier.get(), source, type, target));
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
            nodeState.getLabels()
                .load(labels);
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
        if (!remoteRelationship.getState()
            .isLoaded()) {
            remoteRelationship.getState()
                .load(relationship.asMap());
        }
        return remoteRelationship;
    }

    public void update(long id, RemoteNode remoteNode) {
        update(id, remoteNode, nodeCache);
    }

    public void update(long id, RemoteRelationship remoteRelationship) {
        update(id, remoteRelationship, relationshipCache);
    }

    private <C extends AbstractRemotePropertyContainer<S>, S extends AbstractPropertyContainerState> void update(long id, C propertyContainer,
        Cache<Long, C> cache) {
        cache.invalidate(propertyContainer.getId());
        propertyContainer.updateId(id);
        cache.put(id, propertyContainer);
    }
}
