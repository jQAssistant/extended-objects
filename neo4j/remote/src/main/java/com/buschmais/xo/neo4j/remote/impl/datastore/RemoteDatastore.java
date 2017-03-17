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

    private Driver driver;

    private StatementConfig statementConfig;

    public RemoteDatastore(XOUnit xoUnit) {
        URI uri = xoUnit.getUri();
        Properties properties = xoUnit.getProperties();
        this.driver = getDriver(uri, properties);
        this.statementConfig = getStatementConfig(properties);
    }

    private Driver getDriver(URI uri, Properties properties) {
        String username = (String) properties.get("neo4j.remote.username");
        String password = (String) properties.get("neo4j.remote.password");
        String encryptionLevel = (String) properties.get("neo4j.remote.encryptionLevel");
        String trustStrategy = (String) properties.get("neo4j.remote.trust.strategy");
        String trustCertificate = (String) properties.get("neo4j.remote.trust.certificate");
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
        String statementLogLevel = (String) properties.get("neo4j.remote.statement.log");
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
