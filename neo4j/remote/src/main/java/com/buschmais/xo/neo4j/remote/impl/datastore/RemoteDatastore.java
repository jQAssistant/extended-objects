package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.neo4j.driver.v1.*;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jDatastore;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.logging.LogStrategy;
import com.google.common.base.CaseFormat;

public class RemoteDatastore extends AbstractNeo4jDatastore<RemoteLabel, RemoteRelationshipType, RemoteDatastoreSession> {

    /**
     * Defines the properties supported by this datastore.
     */
    enum Property {

        USERNAME("username"), PASSWORD("password"), ENCRYPTION_LEVEL("encryptionLevel"), TRUST_STRATEGY("trust.strategy"), TRUST_CERTIFICATE(
                "trust.certificate"), STATEMENT_LOG("statement.log");

        private String name;

        Property(String name) {
            this.name = "neo4j.remote." + name;
        }

        String get(Properties properties) {
            return properties.getProperty(name);
        }
    }

    private Driver driver;

    private StatementConfig statementConfig;

    public RemoteDatastore(XOUnit xoUnit) {
        URI uri = xoUnit.getUri();
        Properties properties = xoUnit.getProperties();
        this.driver = getDriver(uri, properties);
        this.statementConfig = getStatementConfig(properties);
    }

    private Driver getDriver(URI uri, Properties properties) {
        String username = Property.USERNAME.get(properties);
        String password = Property.PASSWORD.get(properties);
        String encryptionLevel = Property.ENCRYPTION_LEVEL.get(properties);
        String trustStrategy = Property.TRUST_STRATEGY.get(properties);
        String trustCertificate = Property.TRUST_CERTIFICATE.get(properties);
        Config.ConfigBuilder configBuilder = Config.build();
        if (encryptionLevel != null) {
            configBuilder.withEncryptionLevel(getEnumOption(Config.EncryptionLevel.class, encryptionLevel));
        }
        if (trustStrategy != null) {
            switch (getEnumOption(Config.TrustStrategy.Strategy.class, trustStrategy)) {
            case TRUST_ALL_CERTIFICATES:
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustAllCertificates());
                break;
            case TRUST_CUSTOM_CA_SIGNED_CERTIFICATES:
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustCustomCertificateSignedBy(new File(trustCertificate)));
            case TRUST_SYSTEM_CA_SIGNED_CERTIFICATES:
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustSystemCertificates());
            default:
                throw new XOException("Trust strategy not supported: " + trustStrategy);
            }
        }
        AuthToken authToken = username != null ? AuthTokens.basic(username, password) : null;
        return GraphDatabase.driver(uri, authToken, configBuilder.toConfig());
    }

    private StatementConfig getStatementConfig(Properties properties) {
        StatementConfig.StatementConfigBuilder statementConfigBuilder = StatementConfig.builder();
        String statementLogLevel = Property.STATEMENT_LOG.get(properties);
        if (statementLogLevel != null) {
            statementConfigBuilder.statementLogger(getEnumOption(LogStrategy.class, statementLogLevel));
        }
        return statementConfigBuilder.build();
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
        return new RemoteDatastoreSession(session, statementConfig);
    }

    @Override
    public void close() {
        driver.close();
    }

    private <E extends Enum<E>> E getEnumOption(Class<E> enumType, String value) {
        String normalizedValue = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, value);
        return Enum.valueOf(enumType, normalizedValue);
    }
}
