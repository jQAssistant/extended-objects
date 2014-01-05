package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.spi.metadata.method.EnumPropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

/**
 * Defines the datastore specific interface to get/set properties or relations.
 *
 * @param <Entity>                    The type of entities used by the datastore.
 * @param <Relation>                  The type of relations used by the datastore.
 * @param <PrimitivePropertyMetadata>
 *                                    The type of metadata for primitive properties used by the datastore.
 * @param <EnumPropertyMetadata>
 *                                    The type of metadata for enum properties used by the datastore.
 * @param <RelationMetadata> The type of metadata for relations used by the datastore.
 */
public interface DatastorePropertyManager<Entity, Relation, PrimitivePropertyMetadata, EnumPropertyMetadata, RelationMetadata> {

    /**
     * Set the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     * @param value    The value
     */
    void setProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata, Object value);

    /**
     * Determine if the value of a primitive property is set.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    boolean hasProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Remove the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    void removeProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Get the value of a primitive property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    Object getProperty(Entity entity, PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata> metadata);

    /**
     * Get the value of an enumeration property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     */
    Enum<?> getEnumProperty(Entity entity, EnumPropertyMethodMetadata<EnumPropertyMetadata> metadata);

    /**
     * Set the value of an enumeration property.
     *
     * @param entity   The entity.
     * @param metadata The property metadata.
     * @param value    The value
     */
    void setEnumProperty(Entity entity, EnumPropertyMethodMetadata<EnumPropertyMetadata> metadata, Enum<?> value);


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
     * Return the source of a relation (i.e. where the direction is {@link com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata.Direction#INCOMING}.
     *
     * @param relation The relation.
     * @return The source entity.
     */
    Entity getSource(Relation relation);

    /**
     * Return the target of a relation (i.e. where the direction is {@link com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata.Direction#OUTGOING}.
     *
     * @param relation The relation.
     * @return The target entity.
     */
    Entity getTarget(Relation relation);

}
