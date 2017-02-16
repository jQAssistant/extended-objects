package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.net.URI;
import java.util.Properties;

import org.neo4j.driver.v1.*;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jDatastore;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;

public class RemoteDatastore extends AbstractNeo4jDatastore<RemoteLabel, RemoteRelationshipType, RemoteDatastoreSession> {

    private Driver driver;

    public RemoteDatastore(XOUnit xoUnit) {
        URI uri = xoUnit.getUri();
        Properties properties = xoUnit.getProperties();
        String username = (String) properties.get("neo4j.remote.username");
        String password = (String) properties.get("neo4j.remote.password");
        String encryptionLevel = (String) properties.get("neo4j.remote.encryptionLevel");
        Config.ConfigBuilder configBuilder = Config.build();
        if (encryptionLevel != null) {
            configBuilder.withEncryptionLevel(Config.EncryptionLevel.valueOf(encryptionLevel.toUpperCase()));
        }
        AuthToken authToken = username != null ? AuthTokens.basic(username, password) : null;
        this.driver = GraphDatabase.driver(uri, authToken, configBuilder.toConfig());
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
        driver.close();
    }
}
