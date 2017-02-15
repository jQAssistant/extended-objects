package com.buschmais.xo.neo4j.remote.impl.datastore;

import java.lang.annotation.Annotation;

import org.neo4j.driver.v1.Session;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.remote.impl.model.*;
import com.buschmais.xo.neo4j.spi.metadata.NodeMetadata;
import com.buschmais.xo.neo4j.spi.metadata.PropertyMetadata;
import com.buschmais.xo.neo4j.spi.metadata.RelationshipMetadata;
import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.session.XOSession;

public class RemoteDatastoreSession implements
        DatastoreSession<Long, RemoteNode, NodeMetadata<RemoteLabel>, RemoteLabel, Long, RemoteRelationship, RelationshipMetadata<RemoteRelationshipType>, RemoteRelationshipType, PropertyMetadata> {

    private final Session session;
    private final RemoteDatastoreTransaction transaction;
    private final StatementExecutor statementExecutor;
    private final RemoteDatastoreEntityManager entityManager;
    private final RemoteDatastoreRelationManager relationManager;
    private final RemoteDatastoreSessionCache datastoreSessionCache;

    public RemoteDatastoreSession(Session session) {
        this.session = session;
        transaction = new RemoteDatastoreTransaction(session);
        statementExecutor = new StatementExecutor(transaction);
        datastoreSessionCache = new RemoteDatastoreSessionCache();
        entityManager = new RemoteDatastoreEntityManager(statementExecutor, datastoreSessionCache);
        relationManager = new RemoteDatastoreRelationManager(entityManager, statementExecutor, datastoreSessionCache);
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
        return null;
    }

    @Override
    public void close() {
        session.close();
    }
}
