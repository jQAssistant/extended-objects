package com.buschmais.xo.test.trace.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.NativeQuery;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.datastore.DatastoreSession;
import com.buschmais.xo.spi.datastore.DatastoreTransaction;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

/**
 * {@link DatastoreSession} implementation allowing tracing on delegates.
 */
public class TraceDatastoreSession<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> implements DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> {

    private final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> delegate;

    private final InterceptorFactory interceptorFactory;

    public TraceDatastoreSession(final DatastoreSession<EntityId, Entity, EntityMetadata, EntityDiscriminator, RelationId, Relation, RelationMetadata, RelationDiscriminator> delegate, final InterceptorFactory interceptorFactory) {
        this.delegate = delegate;
        this.interceptorFactory = interceptorFactory;
    }

    @Override
    public DatastoreTransaction getDatastoreTransaction() {
        final DatastoreTransaction delegateDatastoreTransaction = delegate.getDatastoreTransaction();
        return new TraceDatastoreTransaction(interceptorFactory.addInterceptor(delegateDatastoreTransaction, DatastoreTransaction.class));
    }

    @Override
    public boolean isEntity(final Object o) {
        return delegate.isEntity(o);
    }

    @Override
    public boolean isRelation(final Object o) {
        return delegate.isRelation(o);
    }

    @Override
    public Set<EntityDiscriminator> getEntityDiscriminators(final Entity entity) {
        return delegate.getEntityDiscriminators(entity);
    }

    @Override
    public RelationDiscriminator getRelationDiscriminator(final Relation relation) {
        return delegate.getRelationDiscriminator(relation);
    }

    @Override
    public EntityId getEntityId(final Entity entity) {
        return delegate.getEntityId(entity);
    }

    @Override
    public RelationId getRelationId(final Relation relation) {
        return delegate.getRelationId(relation);
    }

    @Override
    public Entity createEntity(final TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, final Set<EntityDiscriminator> entityDiscriminators) {
        return delegate.createEntity(types, entityDiscriminators);
    }

    @Override
    public void deleteEntity(final Entity entity) {
        delegate.deleteEntity(entity);
    }

    @Override
    public ResultIterator<Entity> findEntity(final EntityTypeMetadata<EntityMetadata> type, final EntityDiscriminator entityDiscriminator, final Object value) {
        return delegate.findEntity(type, entityDiscriminator, value);
    }

    @Override
    public ResultIterator<Map<String, Object>> executeQuery(final NativeQuery query, final Map<String, Object> parameters) {
        return delegate.executeQuery(query, parameters);
    }

    @Override
    public void migrateEntity(final Entity entity, final TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, final Set<EntityDiscriminator> entityDiscriminators, final TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> targetTypes, final Set<EntityDiscriminator> targetDiscriminators) {
        delegate.migrateEntity(entity, types, entityDiscriminators, targetTypes, targetDiscriminators);
    }

    @Override
    public void flushEntity(final Entity entity) {
        delegate.flushEntity(entity);
    }

    @Override
    public void flushRelation(final Relation relation) {
        delegate.flushRelation(relation);
    }

    @Override
    public DatastorePropertyManager<Entity, Relation, ?, RelationMetadata> getDatastorePropertyManager() {
        final DatastorePropertyManager<Entity, Relation, ?, RelationMetadata> delegateDatastorePropertyManager = delegate.getDatastorePropertyManager();
        return new TraceDatastorePropertyManager<>(interceptorFactory.addInterceptor(delegateDatastorePropertyManager, DatastorePropertyManager.class));
    }

    @Override
    public NativeQuery<?> getNativeQuery(final String expression, final Class<? extends Annotation> language) {
        return delegate.getNativeQuery(expression, language);
    }

    @Override
    public <QL> NativeQuery<?> getNativeQuery(final AnnotatedElement expression, final Class<? extends Annotation> language) {
        return delegate.getNativeQuery(expression, language);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
