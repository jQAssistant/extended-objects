package com.buschmais.xo.neo4j.remote.impl;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.Datastore;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.metadata.type.TypeMetadata;
import org.neo4j.driver.v1.*;

public class RemoteDatastore implements
        Datastore<RemoteDatastoreSession, NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> {

    private Driver driver;

    public RemoteDatastore(XOUnit xoUnit) {
        URI uri = xoUnit.getUri(); // "bolt://localhost:7687"
        Properties properties = xoUnit.getProperties();
        String username = (String) properties.get("neo4j.remote.username");
        String password = (String) properties.get("neo4j.remote.password");
        this. driver = GraphDatabase.driver( uri, AuthTokens.basic( username, password ) );
    }

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
        Session session = driver.session();
        return new RemoteDatastoreSession(session);
    }

    @Override
    public void close() {
    }
}
