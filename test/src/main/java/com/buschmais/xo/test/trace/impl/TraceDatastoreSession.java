package com.buschmais.xo.test.trace.impl;

import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;

import java.lang.annotation.Annotation;

/**
 * {@link DatastoreSession} implementation allowing tracing on delegates.
 */
public class TraceDatastoreSession<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> {

    private DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> delegate;

    private InterceptorFactory interceptorFactory;

    public TraceDatastoreSession(DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> delegate, InterceptorFactory interceptorFactory) {
        this.delegate = delegate;
        this.interceptorFactory = interceptorFactory;
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        DatastoreTransaction delegateDatastoreTransaction = delegate.getDatastoreTransaction();
        return new TraceDatastoreTransaction(interceptorFactory.addInterceptor(delegateDatastoreTransaction, DatastoreTransaction.class));
    }

    @Override
    public DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?> getDatastoreEntityManager() {
        DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, ?> delegate = this.delegate.getDatastoreEntityManager();
        return new TraceDatastoreEntityManager<>(interceptorFactory.addInterceptor(delegate, DatastoreEntityManager.class));
    }

    @Override
    public DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, ?> getDatastoreRelationManager() {
        DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, ?> delegate = this.delegate.getDatastoreRelationManager();
        return new TraceDatastoreRelationManager<>(interceptorFactory.addInterceptor(delegate, DatastoreRelationManager.class));
    }

    @Override
    public Class<? extends Annotation> getDefaultQueryLanguage() {
        return delegate.getDefaultQueryLanguage();
    }

    @Override
    public <QL extends Annotation> DatastoreQuery<QL> createQuery(Class<QL> queryLanguage) {
        return delegate.createQuery(queryLanguage);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
