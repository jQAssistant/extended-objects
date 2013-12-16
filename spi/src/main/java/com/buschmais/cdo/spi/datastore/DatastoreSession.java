package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.api.ResultIterator;

import java.util.Map;
import java.util.Set;

/**
 * Defines the interface of a datastore session, e.g. a connection to the datastore.
 *
 * @param <EntityId>      The type of entity ids used by the datastore.
 * @param <Entity>        The type of entities used by the datastore.
 * @param <Discriminator> The type of entity discriminators used by the datastore.
 * @param <RelationId>    The type of relation ids used by the datastore.
 * @param <Relation>      The type of relations used by the datastore.
 */
public interface DatastoreSession<EntityId, Entity, Discriminator, RelationId, Relation> {

    DatastoreTransaction getDatastoreTransaction();

    boolean isEntity(Object o);

    Set<Discriminator> getDiscriminators(Entity entity);

    EntityId getId(Entity entity);

    Entity create(TypeSet types, Set<Discriminator> discriminators);

    void delete(Entity node);

    ResultIterator<Entity> find(Class<?> type, Object value);

    <QL> ResultIterator<Map<String, Object>> execute(QL query, Map<String, Object> parameters);

    void migrate(Entity entity, TypeSet types, TypeSet targetTypes);

    void flush(Iterable<Entity> entities);

    // properties

    DatastorePropertyManager getDatastorePropertyManager();

}
