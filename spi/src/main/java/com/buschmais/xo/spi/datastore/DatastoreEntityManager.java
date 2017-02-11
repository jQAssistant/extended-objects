package com.buschmais.xo.spi.datastore;

import java.util.Map;
import java.util.Set;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

/**
 * Defines the interface for all entity related datastore operations.
 */
public interface DatastoreEntityManager<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, PropertyMetadata>
        extends DatastorePropertyManager<Entity, PropertyMetadata> {

    /**
     * Determine if the given object is an entity.
     *
     * @param o
     *            The object.
     * @return <code>true</code> if the object is an entity, <code>false</code>
     *         otherwise.
     */
    boolean isEntity(Object o);

    /**
     * Return the type discriminators of an entity.
     *
     * @param entity
     *            The entity.
     * @return The set of all type discriminators associated with the entity.
     */
    Set<EntityDiscriminator> getEntityDiscriminators(Entity entity);

    /**
     * Return the id of an entity.
     *
     * @param entity
     *            The entity.
     * @return The id of the entity.
     */
    EntityId getEntityId(Entity entity);

    /**
     * Create a new entity for the given types using a set of discriminators
     * representing these types.
     *
     * @param types
     *            The types.
     * @param discriminators
     *            The set of discriminators.
     * @param exampleEntity
     *            The example entity.
     * @return The created entity.
     */
    Entity createEntity(TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> discriminators,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> exampleEntity);

    /**
     * Delete an entity.
     *
     * @param entity
     *            The entity to deleteEntity.
     */
    void deleteEntity(Entity entity);

    /**
     * Find an entity using its id.
     *
     * @param metadata
     *            The metadata of the type.
     * @param discriminator
     *            The discriminator to find the entity.
     * @param id
     *            The id.
     * @return The entity.
     */
    Entity findEntityById(EntityTypeMetadata<EntityMetadata> metadata, EntityDiscriminator discriminator, EntityId id);

    /**
     * Find entities using given primitive property values.
     *
     * @param type
     *            The type of the instances.
     * @param discriminator
     *            The discriminator to find the entities.
     * @param values
     *            The primitive value.
     * @return An iterator returning matching entities.
     */
    ResultIterator<Entity> findEntity(EntityTypeMetadata<EntityMetadata> type, EntityDiscriminator discriminator,
            Map<PrimitivePropertyMethodMetadata<PropertyMetadata>, Object> values);

    /**
     * Add a set of discriminators to an entity.
     * 
     * @param entity
     *            The entity.
     * @param discriminators
     *            The set of discriminators
     */
    void addDiscriminators(Entity entity, Set<EntityDiscriminator> discriminators);

    /**
     * Remove a set of discriminators from an entity.
     *
     * @param entity
     *            The entity.
     * @param discriminators
     *            The set of discriminators
     */
    void removeDiscriminators(Entity entity, Set<EntityDiscriminator> discriminators);

}
