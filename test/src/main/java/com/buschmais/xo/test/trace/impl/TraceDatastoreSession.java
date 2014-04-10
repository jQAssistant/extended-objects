package com.buschmais.xo.test.trace.impl;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.datastore.*;
import com.buschmais.xo.spi.interceptor.InterceptorFactory;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

import java.util.Map;
import java.util.Set;

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
    public boolean isEntity(Object o) {
        return delegate.isEntity(o);
    }

    @Override
    public boolean isRelation(Object o) {
        return delegate.isRelation(o);
    }

    public Set<EntityDiscriminator> getEntityDiscriminators(Entity entity) {
        return delegate.getEntityDiscriminators(entity);
    }

    public RelationDiscriminator getRelationDiscriminator(Relation relation) {
        return delegate.getRelationDiscriminator(relation);
    }

    public EntityId getEntityId(Entity entity) {
        return delegate.getEntityId(entity);
    }

    public RelationId getRelationId(Relation relation) {
        return delegate.getRelationId(relation);
    }

    public Entity createEntity(TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> entityDiscriminators) {
        return delegate.createEntity(types, entityDiscriminators);
    }

    public void deleteEntity(Entity entity) {
        delegate.deleteEntity(entity);
    }

    public ResultIterator<Entity> findEntity(EntityTypeMetadata<EntityMetadata> type, EntityDiscriminator entityDiscriminator, Object value) {
        return delegate.findEntity(type, entityDiscriminator, value);
    }

    @Override
    public <QL> ResultIterator<Map<String, Object>> executeQuery(QL query, Map<String, Object> parameters) {
        return delegate.executeQuery(query, parameters);
    }

    public void migrateEntity(Entity entity, TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> entityDiscriminators, TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> targetTypes, Set<EntityDiscriminator> targetDiscriminators) {
        delegate.migrateEntity(entity, types, entityDiscriminators, targetTypes, targetDiscriminators);
    }

    public void flushEntity(Entity entity) {
        delegate.flushEntity(entity);
    }

    public void flushRelation(Relation relation) {
        delegate.flushRelation(relation);
    }

    @Override
    public DatastorePropertyManager<Entity, Relation, ?, RelationMetadata> getDatastorePropertyManager() {
        DatastorePropertyManager<Entity, Relation, ?, RelationMetadata> delegateDatastorePropertyManager = delegate.getDatastorePropertyManager();
        return new TraceDatastorePropertyManager<>(interceptorFactory.addInterceptor(delegateDatastorePropertyManager, DatastorePropertyManager.class));
    }

    @Override
    public void close() {
        delegate.close();
    }
}
