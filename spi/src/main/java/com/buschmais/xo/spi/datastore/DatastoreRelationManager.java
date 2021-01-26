package com.buschmais.xo.spi.datastore;

import java.util.Map;

import com.buschmais.xo.api.metadata.type.DatastoreRelationMetadata;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.api.metadata.type.RelationTypeMetadata;

/**
 * Defines the interface for all relation related datastore operations.
 */
public interface DatastoreRelationManager<Entity, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator, PrimitivePropertyMetadata>
        extends DatastorePropertyManager<Relation, PrimitivePropertyMetadata> {
    /**
     * Determine if the given object is a relation.
     *
     * @param o
     *            The object.
     * @return <code>true</code> if the object is a relation, <code>false</code>
     *         otherwise.
     */
    boolean isRelation(Object o);

    /**
     * Return the discriminiator for the given relation.
     *
     * @param relation
     *            The relation.
     * @return The discriminator.
     */
    RelationDiscriminator getRelationDiscriminator(Relation relation);

    /**
     * Create a relations (i.e. within collections) between entities.
     *
     * @param source
     *            The source.
     * @param metadata
     *            The relation metadata.
     * @param direction
     *            The direction.
     * @return The relations.
     */
    Relation createRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction, Entity target,
            Map<PrimitivePropertyMethodMetadata<PrimitivePropertyMetadata>, Object> exampleEntity);

    /**
     * Delete a relation between entities.
     *
     * @param relation
     *            The relation.
     */
    void deleteRelation(Relation relation);

    /**
     * Return the id of a relation.
     *
     * @param relation
     *            The relation.
     * @return The id of the relation.
     */
    RelationId getRelationId(Relation relation);

    /**
     * Find a relation using its id.
     *
     * @param metadata
     *            The metadata of the type.
     * @param id
     *            The id.
     * @return The relation.
     */
    Relation findRelationById(RelationTypeMetadata<RelationMetadata> metadata, RelationId id);

    /**
     * Return a single relation (i.e. representing a direct reference) between two
     * entities.
     *
     * @param source
     *            The entity.
     * @param metadata
     *            The relation metadata.
     * @param direction
     *            The direction.
     * @return The relation.
     */
    Relation getSingleRelation(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction);

    /**
     * Return all relations (i.e. representing collections) between entities.
     *
     * @param source
     *            The source.
     * @param metadata
     *            The relation metadata.
     * @param direction
     *            The direction.
     * @return The relations.
     */
    Iterable<Relation> getRelations(Entity source, RelationTypeMetadata<RelationMetadata> metadata, RelationTypeMetadata.Direction direction);

    /**
     * Return the source of a relation (i.e. where the direction is
     * {@link com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction#TO}
     * .
     *
     * @param relation
     *            The relation.
     * @return The source entity.
     */
    Entity getFrom(Relation relation);

    /**
     * Return the target of a relation (i.e. where the direction is
     * {@link com.buschmais.xo.api.metadata.type.RelationTypeMetadata.Direction#FROM}
     * .
     *
     * @param relation
     *            The relation.
     * @return The target entity.
     */
    Entity getTo(Relation relation);

}
