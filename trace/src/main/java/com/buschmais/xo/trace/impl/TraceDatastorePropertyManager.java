package com.buschmais.xo.trace.impl;

import com.buschmais.xo.spi.datastore.DatastorePropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

/**
 * {@link DatastorePropertyManager} implementation allowing tracing on delegates.
 */
public class TraceDatastorePropertyManager<Entity, Relation, PrimitivePropertyMetadata, RelationMetadata> implements DatastorePropertyManager<Entity, Relation, PrimitivePropertyMetadata, RelationMetadata> {

    private DatastorePropertyManager<Entity, Relation, PrimitivePropertyMetadata, RelationMetadata> delegate;

    public TraceDatastorePropertyManager(DatastorePropertyManager<Entity, Relation, PrimitivePropertyMetadata, RelationMetadata> delegate) {
        this.delegate = delegate;
    }

    public void setEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value) {
        delegate.setEntityProperty(entity, metadata, value);
    }

    public void setRelationProperty(Relation relation, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value) {
        delegate.setRelationProperty(relation, metadata, value);
    }

    public boolean hasEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return delegate.hasEntityProperty(entity, metadata);
    }

    public boolean hasRelationProperty(Relation relation, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return delegate.hasRelationProperty(relation, metadata);
    }

    public void removeEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        delegate.removeEntityProperty(entity, metadata);
    }

    public void removeRelationProperty(Relation relation, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        delegate.removeRelationProperty(relation, metadata);
    }

    public Object getEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return delegate.getEntityProperty(entity, metadata);
    }

    public Object getRelationProperty(Relation relation, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata) {
        return delegate.getRelationProperty(relation, metadata);
    }

    public boolean hasSingleRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return delegate.hasSingleRelation(source, metadata, direction);
    }

    public Relation getSingleRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return delegate.getSingleRelation(source, metadata, direction);
    }

    public Iterable<Relation> getRelations(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return delegate.getRelations(source, metadata, direction);
    }

    public Relation createRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction, Entity target) {
        return delegate.createRelation(source, metadata, direction, target);
    }

    public void deleteRelation(Relation relation) {
        delegate.deleteRelation(relation);
    }

    public Entity getFrom(Relation relation) {
        return delegate.getFrom(relation);
    }

    public Entity getTo(Relation relation) {
        return delegate.getTo(relation);
    }
}
