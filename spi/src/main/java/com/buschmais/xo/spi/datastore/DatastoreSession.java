package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;

import java.util.Map;
import java.util.Set;

/**
 * Defines the interface of a datastore session, e.g. a connection to the datastore.
 *
 * @param <EntityId>            The type of entity ids used by the datastore.
 * @param <Entity>              The type of entities used by the datastore.
 * @param <EntityDiscriminator> The type of entity discriminators used by the datastore.
 * @param <RelationId>          The type of relation ids used by the datastore.
 * @param <Relation>            The type of relations used by the datastore.
 */
public interface DatastoreSession<EntityId, Entity, EntityMetadata extends DatastoreEntityMetadata<EntityDiscriminator>, EntityDiscriminator, RelationId, Relation, RelationMetadata extends DatastoreRelationMetadata<RelationDiscriminator>, RelationDiscriminator> {

    /**
     * Return the instance of the datastore transaction associated with the session.
     *
     * @return The datastore transaction.
     */
    DatastoreTransaction getDatastoreTransaction();

    /**
     * Determine if the given object is an entity.
     *
     * @param o The object.
     * @return <code>true</code> if the object is an entity, <code>false</code> otherwise.
     */
    boolean isEntity(Object o);

    /**
     * Determine if the given object is a relation.
     *
     * @param o The object.
     * @return <code>true</code> if the object is a relation, <code>false</code> otherwise.
     */
    boolean isRelation(Object o);

    /**
     * Return the type discriminators of an entity.
     *
     * @param entity The entity.
     * @return The set of all type discriminators associated with the entity.
     */
    Set<EntityDiscriminator> getEntityDiscriminators(Entity entity);

    /**
     * Return the discriminiator for the given relation.
     *
     * @param relation The relation.
     * @return The discriminator.
     */
    RelationDiscriminator getRelationDiscriminator(Relation relation);

    /**
     * Return the id of an entity.
     *
     * @param entity The entity.
     * @return The id of the entity.
     */
    EntityId getEntityId(Entity entity);

    /**
     * Return the id of a relation.
     *
     * @param relation The relation.
     * @return The id of the relation.
     */
    RelationId getRelationId(Relation relation);

    /**
     * Create a new entity for the given types using a set of discriminators representing these types.
     *
     * @param types          The types.
     * @param discriminators The set of discriminators.
     * @return The created entity.
     */
    Entity createEntity(TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> discriminators);

    /**
     * Delete an entity.
     *
     * @param entity The entity to deleteEntity.
     */
    void deleteEntity(Entity entity);

    /**
     * Find entities using a single primitive value (e.g. from an index if supported by the datastore).
     *
     * @param type          The type of the instances.
     * @param discriminator The discriminator to find the entities.
     * @param value         The primitive value (e.g. indexed value).
     * @return An iterator returning matching entities.
     */
    ResultIterator<Entity> findEntity(EntityTypeMetadata<EntityMetadata> type, EntityDiscriminator discriminator, Object value);

    /**
     * Execute a query.
     *
     * @param query      The query.
     * @param parameters The parameters of the query.
     * @param <QL>       The query language type.
     * @return A {@link ResultIterator} instance returning each row as a map of colum names and their values.
     */
    <QL> ResultIterator<Map<String, Object>> executeQuery(QL query, Map<String, Object> parameters);

    /**
     * Migrate an entity of a given type and discriminators to the given target types and target discriminators.
     *
     * @param entity               The entity to migrate.
     * @param types                The entity types before migration.
     * @param discriminators       The discriminators of the entity before migration.
     * @param targetTypes          The entity types after migration.
     * @param targetDiscriminators The discriminators of the entity after migration.
     */
    void migrateEntity(Entity entity, TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> types, Set<EntityDiscriminator> discriminators, TypeMetadataSet<EntityTypeMetadata<EntityMetadata>> targetTypes, Set<EntityDiscriminator> targetDiscriminators);

    /**
     * Flush the given entity to the datastore.
     *
     * @param entity The entity to flushEntity.
     */
    void flushEntity(Entity entity);

    /**
     * Flush the given relation to the datastore.
     *
     * @param relation The relation to flushEntity.
     */
    void flushRelation(Relation relation);

    /**
     * Return the {@link DatastorePropertyManager} associated with this datastore session.
     *
     * @return The {@link DatastorePropertyManager}.
     */
    DatastorePropertyManager<Entity, Relation, ?, RelationMetadata> getDatastorePropertyManager();

    /**
     * Close the session.
     */
    void close();
}
