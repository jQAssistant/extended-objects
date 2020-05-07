package com.buschmais.xo.trace.impl;

import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.datastore.DatastoreEntityManager;
import com.buschmais.xo.spi.datastore.DatastoreEntityMetadata;
import com.buschmais.xo.spi.datastore.DynamicType;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

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
    public Entity createEntity(DynamicType<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> entityDiscriminators,
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

    @Override
    public void addDiscriminators(DynamicType<EntityTypeMetadata<EntityMetadata>> types, Entity entity, Set<EntityDiscriminator> discriminators) {
        delegate.addDiscriminators(types, entity, discriminators);
    }

    @Override
    public void removeDiscriminators(DynamicType<EntityTypeMetadata<EntityMetadata>> removedTypes, Entity entity, Set<EntityDiscriminator> discriminators) {
        delegate.removeDiscriminators(removedTypes, entity, discriminators);
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

    @Override
    public void flush(Iterable<Entity> entities) {
        delegate.flush(entities);
    }

    @Override
    public void afterCompletion(Entity entity, boolean clear) {
        delegate.afterCompletion(entity, clear);
    }
}
