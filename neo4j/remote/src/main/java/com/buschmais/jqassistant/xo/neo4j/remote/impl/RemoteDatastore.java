package com.buschmais.jqassistant.xo.neo4j.remote.impl;

import java.util.Map;

import com.buschmais.jqassistant.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.jqassistant.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;

public class RemoteDatastore implements
        Datastore<RemoteDatastoreSession, NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> {

    @Override
    public void init(Map<Class<?>, TypeMetadata> registeredMetadata) {
    }

    @Override
    public DatastoreMetadataFactory<NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> getMetadataFactory() {
        return new AbstractNeo4jMetadataFactory<RemoteLabel, RemoteRelationshipType>() {
            @Override
            protected RemoteRelationshipType createRelationshipType(String name) {
                return new RemoteRelationshipType(name);
            }

            @Override
            protected RemoteLabel createLabel(String name) {
                return new RemoteLabel(name);
            }
        };
    }

    @Override
    public RemoteDatastoreSession createSession() {
        return null;
    }

    @Override
    public void close() {
    }
}
