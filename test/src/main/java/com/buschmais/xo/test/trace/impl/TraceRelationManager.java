package com.buschmais.xo.test.trace.impl;

import com.buschmais.xo.spi.datastore.DatastoreRelationManager;
import com.buschmais.xo.spi.datastore.DatastoreRelationMetadata;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

import java.util.Map;

/**
 * Implementation of a
 * {@link com.buschmais.xo.spi.datastore.DatastoreRelationManager} which
 * delegates to another implementation.
 */
public class TraceRelationManager<Entity, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PropertyMetadata>
        implements DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> {

    private DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> delegate;

    /**
     * Constructor.
     *
     * @param delegate The delegate.
     */
    public TraceRelationManager(
            DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata, RelationDiscriminator, PropertyMetadata> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isRelation(Object o) {
        return delegate.isRelation(o);
    }

    @Override
    public RelationDiscriminator getRelationDiscriminator(Relation relation) {
        return delegate.getRelationDiscriminator(relation);
    }

    @Override
    public Relation createRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction, Entity target, Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> example) {
        return delegate.createRelation(source, metadata, direction, target, example);
    }

    @Override
    public void deleteRelation(Relation relation) {
        delegate.deleteRelation(relation);
    }

    @Override
    public RelationId getRelationId(Relation relation) {
        return delegate.getRelationId(relation);
    }

    @Override
    public void flushRelation(Relation relation) {
        delegate.flushRelation(relation);
    }

    @Override
    public boolean hasSingleRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return delegate.hasSingleRelation(source, metadata, direction);
    }

    @Override
    public Relation getSingleRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return delegate.getSingleRelation(source, metadata, direction);
    }

    @Override
    public Iterable<Relation> getRelations(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction) {
        return delegate.getRelations(source, metadata, direction);
    }

    @Override
    public Entity getFrom(Relation relation) {
        return delegate.getFrom(relation);
    }

    @Override
    public Entity getTo(Relation relation) {
        return delegate.getTo(relation);
    }

    @Override
    public void setProperty(Relation entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata, Object value) {
        delegate.setProperty(entity, metadata, value);
    }

    @Override
    public boolean hasProperty(Relation entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return delegate.hasProperty(entity, metadata);
    }

    @Override
    public void removeProperty(Relation entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        delegate.removeProperty(entity, metadata);
    }

    @Override
    public Object getProperty(Relation entity, PrimitivePropertyMethodMetadata<PropertyMetadata> metadata) {
        return delegate.getProperty(entity, metadata);
    }
}
