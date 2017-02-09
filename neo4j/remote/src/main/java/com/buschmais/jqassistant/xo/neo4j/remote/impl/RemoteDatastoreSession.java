package com.buschmais.jqassistant.xo.neo4j.remote.impl;

import java.lang.annotation.Annotation;

import com.buschmais.jqassistant.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.jqassistant.xo.neo4j.remote.impl.model.RemoteNode;
import com.buschmais.jqassistant.xo.neo4j.remote.impl.model.RemoteRelationship;
import com.buschmais.jqassistant.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.session.XOSession;

public class RemoteDatastoreSession implements
        DatastoreSession<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> {

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return null;
    }

    @Override
    public DatastoreEntityManager<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, PropertyMetadata> getDatastoreEntityManager() {
        return null;
    }

    @Override
    public DatastoreRelationManager<RemoteNode, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> getDatastoreRelationManager() {
        return null;
    }

    @Override
    public Class<? extends Annotation> getDefaultQueryLanguage() {
        return null;
    }

    @Override
    public <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage) {
        return null;
    }

    @Override
    public <R> R createRepository(XOSession xoSession, Class<R> type) {
        return null;
    }

    @Override
    public void close() {

    }
}
