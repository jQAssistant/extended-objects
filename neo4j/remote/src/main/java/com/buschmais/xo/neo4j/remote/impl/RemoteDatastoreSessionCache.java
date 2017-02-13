package com.buschmais.xo.neo4j.remote.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.neo4j.driver.v1.types.Node;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.xo.neo4j.remote.impl.model.state.NodeState;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class RemoteDatastoreSessionCache {

    private Cache<Long, RemoteNode> nodeCache = CacheBuilder.newBuilder().weakValues().build();

    private Cache<Long, RemoteRelationship> relationshipCache = CacheBuilder.newBuilder().weakValues().build();

    public RemoteNode getNode(Node node) {
        try {
            return nodeCache.get(node.id(), () -> new RemoteNode(node.id(), getNodeState(node)));
        } catch (ExecutionException e) {
            throw new XOException("Cannot fetch node");
        }
    };

    public NodeState getNodeState(Node node) {
        Set<RemoteLabel> labels = new HashSet<>();
        for (String label : node.labels()) {
            labels.add(new RemoteLabel(label));
        }
        return new NodeState(labels, new HashMap<>(node.asMap()));
    }

}
