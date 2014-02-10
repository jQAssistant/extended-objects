package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

/**
 * Defines the datastore specific interface to get/set properties or relations.
 *
 * @param <Entity>                    The type of entities used by the datastore.
 * @param <Relation>                  The type of relations used by the datastore.
 * @param <PrimitivePropertyMetadata> The type of metadata for primitive properties used by the datastore.
 * @param <RelationMetadata>          The type of metadata for relations used by the datastore.
 */
public interface DatastorePropertyManager<Entity, Relation, PrimitivePropertyMetadata, RelationMetadata> {

    /**
     * Set the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     * @param value    The value
     */
    void setEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value);

    /**
     * Set the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     * @param value    The value
     */
    void setRelationProperty(Relation entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value);

    /**
     * Determine if the value of a primitive property is set.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    boolean hasEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Determine if the value of a primitive property is set.
     *
     * @param relation The relation.
     * @param metadata The property metadata.
     */
    boolean hasRelationProperty(Relation relation, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Remove the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    void removeEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Remove the value of a primitive property.
     *
     * @param relation The relation.
     * @param metadata The property metadata.
     */
    void removeRelationProperty(Relation relation, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);


    /**
     * Get the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    Object getEntityProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Get the value of a primitive property.
     *
     * @param relation The relation.
     * @param metadata The property metadata.
     */
    Object getRelationProperty(Relation relation, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Determine if a single relation (i.e. direct reference) between two entities exists.
     *
     * @param source    The entity
     * @param metadata  The relation metadata.
     * @param direction The direction.
     * @return <code>true</code> if an relation exists, <code>false</code> otherwise.
     */
    boolean hasSingleRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction);

    /**
     * Return a single relation (i.e. representing a direct reference) between two entities.
     *
     * @param source    The entity.
     * @param metadata  The relation metadata.
     * @param direction The direction.
     * @return The relation.
     */
    Relation getSingleRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction);

    /**
     * Return all relations (i.e. representing collections) between entities.
     *
     * @param source    The source.
     * @param metadata  The relation metadata.
     * @param direction The direction.
     * @return The relations.
     */
    Iterable<Relation> getRelations(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction);

    /**
     * Create a relations (i.e. within collections) between entities.
     *
     * @param source    The source.
     * @param metadata  The relation metadata.
     * @param direction The direction.
     * @return The relations.
     */
    Relation createRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction, Entity target);

    /**
     * Delete a relation between entities.
     *
     * @param relation The relation.
     */
    void deleteRelation(Relation relation);

    /**
     * Return the source of a relation (i.e. where the direction is {@link com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata.Direction#TO}.
     *
     * @param relation The relation.
     * @return The source entity.
     */
    Entity getFrom(Relation relation);

    /**
     * Return the target of a relation (i.e. where the direction is {@link com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata.Direction#FROM}.
     *
     * @param relation The relation.
     * @return The target entity.
     */
    Entity getTo(Relation relation);

}
