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
import com.buschmais.xo.spi.logging.LogLevel;

import org.neo4j.driver.v1.Config;

public class RemoteNeo4jXOProvider
        implements XODatastoreProvider<NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> {

    @Override
    public Datastore<?, NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> createDatastore(
            XOUnit xoUnit) {
        return new RemoteDatastore(xoUnit);
    }

    @Override
    public Class<? extends Enum<? extends ConfigurationProperty>> getConfigurationProperties() {
        return Property.class;
    }

    /**
     * Defines the properties supported by this datastore.
     */
    public enum Property implements ConfigurationProperty {

        USERNAME("username", String.class), PASSWORD("password", String.class), ENCRYPTION_LEVEL("encryptionLevel",
                Config.EncryptionLevel.class), TRUST_STRATEGY("trust.strategy", Config.TrustStrategy.class), TRUST_CERTIFICATE("trust.certificate",
                        String.class), STATEMENT_LOG_LEVEL("statement.log.level",
                                LogLevel.class), STATEMENT_BATCHABLE_DEFAULT("statement.batching.default", Boolean.class);

        public static final String NEO4J_REMOTE = "neo4j.remote.";

        private final String key;

        private final Class<?> type;

        Property(String key, Class<?> type) {
            this.key = NEO4J_REMOTE + key;
            this.type = type;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        public String get(Properties properties) {
            return properties.getProperty(key);
        }

    }
}
