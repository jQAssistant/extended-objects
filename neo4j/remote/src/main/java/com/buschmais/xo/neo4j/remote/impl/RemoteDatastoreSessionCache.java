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

    public RemoteNode getNode(long id, NodeState nodeState) {
        try {
            return nodeCache.get(id, () -> new RemoteNode(id, nodeState));
        } catch (ExecutionException e) {
            throw new XOException("Cannot fetch node");
        }
    }
}
