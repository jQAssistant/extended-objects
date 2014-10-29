package com.buschmais.xo.test.trace.impl;

import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.buschmais.xo.spi.session.XOSession;

import java.lang.annotation.Annotation;

/**
 * {@link DatastoreSession} implementation allowing tracing on delegates.
 */
public class TraceDatastoreSession<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata>
        implements
        DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> {

    private DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> delegate;

    private InterceptorFactory interceptorFactory;

    public TraceDatastoreSession(
            DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> delegate,
            InterceptorFactory interceptorFactory) {
        this.delegate = delegate;
        this.interceptorFactory = interceptorFactory;
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        DatastoreTransaction delegateDatastoreTransaction = delegate.getDatastoreTransaction();
        return delegateDatastoreTransaction != null ? new TraceTransaction(interceptorFactory.addInterceptor(delegateDatastoreTransaction,
                DatastoreTransaction.class)) : null;
    }

    @Override
    public DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, PropertyMetadata> getDatastoreEntityManager() {
        DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, PropertyMetadata> delegate = this.delegate.getDatastoreEntityManager();
        return new TraceEntityManager<>(interceptorFactory.addInterceptor(delegate, DatastoreEntityManager.class));
    }

    @Override
    public DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> getDatastoreRelationManager() {
        DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> delegate = this.delegate
                .getDatastoreRelationManager();
        return new TraceRelationManager<>(interceptorFactory.addInterceptor(delegate, DatastoreRelationManager.class));
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
    public <R> R createRepository(XOSession xoSession, Class<R> type) {
        return delegate.createRepository(xoSession, type);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
