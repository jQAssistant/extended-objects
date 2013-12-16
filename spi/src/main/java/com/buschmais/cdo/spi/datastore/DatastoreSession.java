package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.api.ResultIterator;

import java.util.Map;
import java.util.Set;

public interface DatastoreSession<EntityId, Entity, Discriminator, RelationId, Relation> {

    DatastoreTransaction getDatastoreTransaction();

    boolean isEntity(Object o);

    Set<Discriminator> getDiscriminators(Entity entity);

    EntityId getId(Entity entity);

    Entity create(TypeSet types);

    void delete(Entity node);

    ResultIterator<Entity> find(Class<?> type, Object value);

    <QL> ResultIterator<Map<String, Object>> execute(QL query, Map<String, Object> parameters);

    void migrate(Entity entity, TypeSet types, TypeSet targetTypes);

    void flush(Iterable<Entity> entities);

    // properties

    DatastorePropertyManager getDatastorePropertyManager();

}
