package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.TypedNeo4jRepository;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.remote.impl.converter.RemoteParameterConverter;
import com.buschmais.xo.neo4j.remote.impl.converter.RemoteValueConverter;
import com.buschmais.xo.neo4j.remote.impl.model.*;
import com.buschmais.xo.neo4j.spi.Neo4jDatastoreSession;
import com.buschmais.xo.neo4j.spi.helper.Converter;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreQuery;
import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.buschmais.xo.spi.reflection.ClassHelper;
import com.buschmais.xo.spi.session.XOSession;

public class RemoteDatastoreSession implements Neo4jDatastoreSession<RemoteNode, RemoteLabel, RemoteRelationship, RemoteRelationshipType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDatastoreSession.class);

    private final Session session;
    private final RemoteDatastoreTransaction transaction;
    private final StatementExecutor statementExecutor;
    private final RemoteDatastoreEntityManager entityManager;
    private final RemoteDatastoreRelationManager relationManager;
    private final RemoteDatastoreSessionCache datastoreSessionCache;
    private final Converter parameterConverter;
    private final Converter valueConverter;

    public RemoteDatastoreSession(Session session) {
        this.session = session;
        this.transaction = new RemoteDatastoreTransaction(session);
        this.statementExecutor = new StatementExecutor(transaction);
        this.datastoreSessionCache = new RemoteDatastoreSessionCache();
        this.parameterConverter = new Converter(Arrays.asList(new RemoteParameterConverter()));
        this.valueConverter = new Converter(Arrays.asList(new RemoteValueConverter(datastoreSessionCache)));
        this.entityManager = new RemoteDatastoreEntityManager(statementExecutor, datastoreSessionCache);
        this.relationManager = new RemoteDatastoreRelationManager(entityManager, statementExecutor, datastoreSessionCache);
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        return transaction;
    }

    @Override
    public DatastoreEntityManager<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, PropertyMetadata> getDatastoreEntityManager() {
        return entityManager;
    }

    @Override
    public DatastoreRelationManager<RemoteNode, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> getDatastoreRelationManager() {
        return relationManager;
    }

    @Override
    public Class<? extends Annotation> getDefaultQueryLanguage() {
        return Cypher.class;
    }

    @Override
    public <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage) {
        if (Cypher.class.equals(queryLanguage)) {
            return (DatastoreQuery<QL>) new RemoteDatastoreCypherQuery(statementExecutor, datastoreSessionCache);
        }
        throw new XOException("Unsupported query language: " + queryLanguage.getName());
    }

    @Override
    public <R> R createRepository(XOSession xoSession, Class<R> type) {
        if (TypedNeo4jRepository.class.isAssignableFrom(type)) {
            Class<?> typeParameter = ClassHelper.getTypeParameter(TypedNeo4jRepository.class, type);
            if (typeParameter == null) {
                throw new XOException("Cannot determine type parameter for " + type.getName());
            }
            return (R) new RemoteTypedNeo4jRepositoryImpl<>(xoSession, typeParameter, statementExecutor, datastoreSessionCache);
        }
        return (R) new RemoteNeo4jRepositoryImpl(xoSession, statementExecutor, datastoreSessionCache);
    }

    @Override
    public void close() {
        if (session.isOpen()) {
            session.close();
        } else {
            LOGGER.warn("Session is already closed.");
        }
    }

    @Override
    public Object convertValue(Object value) {
        return valueConverter.convert(value);
    }

    @Override
    public Object convertParameter(Object value) {
        return parameterConverter.convert(value);
    }
}
