package com.buschmais.xo.test.trace.impl;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.TypeMetadataSet;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

import java.util.Map;
import java.util.Set;

/**
 * Implementation of a
 * {@link com.buschmais.xo.spi.datastore.DatastoreEntityManager} which delegates
 * to another implementation.
 */
public class TraceEntityManager<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, PropertyMetadata>
        implements DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, PropertyMetadata> {

    private DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, PropertyMetadata> delegate;

    public TraceEntityManager(DatastoreEntityManager<EntityId, Entity, EntityMetadata, EntityDiscriminator, PropertyMetadata> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isEntity(Object o) {
        return delegate.isEntity(o);
    }

    public Set<EntityDiscriminator> getEntityDiscriminators(Entity entity) {
        return delegate.getEntityDiscriminators(entity);
    }

    public EntityId getEntityId(Entity entity) {
        return delegate.getEntityId(entity);
    }

    @Override
    public Entity createEntity(TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> entityDiscriminators,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity) {
        return delegate.createEntity(types, entityDiscriminators, exampleEntity);
    }

    public void deleteEntity(Entity entity) {
        delegate.deleteEntity(entity);
    }

    @Override
    public Entity findEntityById(EntityTypeMetadata<EntityMetadata> metadata, EntityDiscriminator entityDiscriminator, EntityId entityId) {
        return delegate.findEntityById(metadata, entityDiscriminator, entityId);
    }

    @Override
    public ResultIterator<Entity> findEntity(EntityTypeMetadata<EntityMetadata> type, EntityDiscriminator entityDiscriminator,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> values) {
        return delegate.findEntity(type, entityDiscriminator, values);
    }

    public void migrateEntity(Entity entity, TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> entityDiscriminators,
            TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> targetTypes, Set<EntityDiscriminator> targetDiscriminators) {
        delegate.migrateEntity(entity, types, entityDiscriminators, targetTypes, targetDiscriminators);
    }

    @Override
    public void addDiscriminators(Entity entity, Set<EntityDiscriminator> discriminators) {
        delegate.addDiscriminators(entity, discriminators);
    }

    @Override
    public void removeDiscriminators(Entity entity, Set<EntityDiscriminator> discriminators) {
        delegate.removeDiscriminators(entity, discriminators);
    }

    public void flushEntity(Entity entity) {
        delegate.flushEntity(entity);
    }

    public void setProperty(Entity entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        delegate.setProperty(entity, metadata, value);
    }

    public boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return delegate.hasProperty(entity, metadata);
    }

    public void removeProperty(Entity entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        delegate.removeProperty(entity, metadata);
    }

    public Object getProperty(Entity entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return delegate.getProperty(entity, metadata);
    }
}
