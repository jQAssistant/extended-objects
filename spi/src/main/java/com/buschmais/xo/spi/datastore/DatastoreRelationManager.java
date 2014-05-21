package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.spi.metadata.type.RelationTypeMetadata;

/**
 * Created by dimahler on 5/21/2014.
 */
public interface DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PrimitivePropertyMetadata> extends DatastoreElementManager<Relation, PrimitivePropertyMetadata> {
    /**
     * Determine if the given object is a relation.
     *
     * @param o The object.
     * @return <code>true</code> if the object is a relation, <code>false</code> otherwise.
     */
    boolean isRelation(Object o);

    /**
     * Return the discriminiator for the given relation.
     *
     * @param relation The relation.
     * @return The discriminator.
     */
    RelationDiscriminator getRelationDiscriminator(Relation relation);

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
     * Return the id of a relation.
     *
     * @param relation The relation.
     * @return The id of the relation.
     */
    RelationId getRelationId(Relation relation);

    /**
     * Flush the given relation to the datastore.
     *
     * @param relation The relation to flushEntity.
     */
    void flushRelation(Relation relation);

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
     * Return the source of a relation (i.e. where the direction is {@link com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction#TO}.
     *
     * @param relation The relation.
     * @return The source entity.
     */
    Entity getFrom(Relation relation);

    /**
     * Return the target of a relation (i.e. where the direction is {@link com.buschmais.xo.spi.metadata.type.RelationTypeMetadata.Direction#FROM}.
     *
     * @param relation The relation.
     * @return The target entity.
     */
    Entity getTo(Relation relation);
}
