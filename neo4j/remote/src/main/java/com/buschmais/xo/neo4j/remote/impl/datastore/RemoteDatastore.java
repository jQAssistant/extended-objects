package com.buschmais.xo.neo4j.remote.impl.datastore;

import static java.lang.Boolean.parseBoolean;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.remote.api.RemoteNeo4jXOProvider;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteLabel;
import com.buschmais.xo.neo4j.remote.impl.model.RemoteRelationshipType;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jDatastore;
import com.buschmais.xo.neo4j.spi.AbstractNeo4jMetadataFactory;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.xo.spi.logging.LogLevel;

import com.google.common.base.CaseFormat;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteDatastore extends AbstractNeo4jDatastore<RemoteLabel, RemoteRelationshipType, RemoteDatastoreSession> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDatastore.class);

    private final Driver driver;

    private final StatementConfig statementConfig;

    public RemoteDatastore(XOUnit xoUnit) {
        URI uri = xoUnit.getUri();
        Properties properties = xoUnit.getProperties();
        this.driver = getDriver(uri, properties);
        this.statementConfig = getStatementConfig(properties);
    }

    private Driver getDriver(URI uri, Properties properties) {
        String username = RemoteNeo4jXOProvider.Property.USERNAME.get(properties);
        String password = RemoteNeo4jXOProvider.Property.PASSWORD.get(properties);
        String encryption = RemoteNeo4jXOProvider.Property.ENCRYPTION.get(properties);
        String trustStrategy = RemoteNeo4jXOProvider.Property.TRUST_STRATEGY.get(properties);
        String trustCertificate = RemoteNeo4jXOProvider.Property.TRUST_CERTIFICATE.get(properties);
        Config.ConfigBuilder configBuilder = Config.builder();
        if (encryption == null || parseBoolean(encryption)) {
            configBuilder.withEncryption();
        }
        if (trustStrategy != null) {
            switch (getEnumOption(Config.TrustStrategy.Strategy.class, trustStrategy)) {
            case TRUST_ALL_CERTIFICATES:
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustAllCertificates());
                break;
            case TRUST_CUSTOM_CA_SIGNED_CERTIFICATES:
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustCustomCertificateSignedBy(new File(trustCertificate)));
                break;
            case TRUST_SYSTEM_CA_SIGNED_CERTIFICATES:
                configBuilder.withTrustStrategy(Config.TrustStrategy.trustSystemCertificates());
                break;
            default:
                throw new XOException("Trust strategy not supported: " + trustStrategy);
            }
        }
        AuthToken authToken = username != null ? AuthTokens.basic(username, password) : null;
        return GraphDatabase.driver(uri, authToken, configBuilder.build());
    }

    private StatementConfig getStatementConfig(Properties properties) {
        StatementConfig.StatementConfigBuilder statementConfigBuilder = StatementConfig.builder();
        String statementLogLevel = RemoteNeo4jXOProvider.Property.STATEMENT_LOG_LEVEL.get(properties);
        if (statementLogLevel != null) {
            statementConfigBuilder.logLevel(getEnumOption(LogLevel.class, statementLogLevel));
        }
        String batchableDefault = RemoteNeo4jXOProvider.Property.STATEMENT_BATCHABLE_DEFAULT.get(properties);
        if (batchableDefault != null) {
            statementConfigBuilder.batchableDefault(parseBoolean(batchableDefault));
        }
        StatementConfig config = statementConfigBuilder.build();
        LOGGER.debug("Using statement configuration {}.", config);
        return config;
    }

    @Override
    public DatastoreMetadataFactory<NodeMetadata<RemoteLabel>, RemoteLabel, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType> getMetadataFactory() {
        return new AbstractNeo4jMetadataFactory<>() {
            @Override
            protected RemoteRelationshipType createRelationshipType(String name) {
                return new RemoteRelationshipType(name);
            }

            @Override
            protected RemoteLabel createLabel(String name) {
                return new RemoteLabel(name);
            }

            @Override
            protected boolean isBatchableDefault() {
                return statementConfig.isBatchableDefault();
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
        try {
            return Enum.valueOf(enumType, normalizedValue);
        } catch (IllegalArgumentException e) {
            List<String> allowedValues = new ArrayList<>();
            for (E allowedValue : enumType.getEnumConstants()) {
                allowedValues.add(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, allowedValue.name()));
            }
            throw new XOException("Unknown value '" + value + "', allowed values are " + allowedValues);
        }
    }
}
