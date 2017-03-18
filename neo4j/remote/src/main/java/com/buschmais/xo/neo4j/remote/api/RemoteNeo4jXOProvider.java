package com.buschmais.xo.neo4j.remote.api;

import java.util.Properties;

import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.remote.impl.datastore.RemoteDatastore;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.bootstrap.XODatastoreProvider;
import com.buschmais.xo.spi.datastore.Datastore;

public class RemoteNeo4jXOProvider
        implements XODatastoreProvider<NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> {

    @Override
    public Datastore<?, NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> createDatastore(
            XOUnit xoUnit) {
        return new RemoteDatastore(xoUnit);
    }

    /**
     * Defines the properties supported by this datastore.
     */
    public enum Property {

        USERNAME("username"), PASSWORD("password"), ENCRYPTION_LEVEL("encryptionLevel"), TRUST_STRATEGY("trust.strategy"), TRUST_CERTIFICATE(
                "trust.certificate"), STATEMENT_LOG_LEVEL("statement.log.level");

        private String name;

        Property(String name) {
            this.name = "neo4j.remote." + name;
        }

        public String get(Properties properties) {
            return properties.getProperty(name);
        }
    }
}
