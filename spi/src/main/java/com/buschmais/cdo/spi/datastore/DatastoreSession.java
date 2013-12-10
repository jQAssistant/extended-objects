package com.buschmais.cdo.spi.datastore;

import com.buschmais.cdo.api.ResultIterator;

import java.util.Map;

public interface DatastoreSession<EntityId, Entity, RelationId, Relation> {

    DatastoreTransaction getDatastoreTransaction();

    boolean isEntity(Object o);

    EntityId getId(Entity entity);

    Entity create(TypeSet types);

    void delete(Entity node);

    ResultIterator<Entity> find(Class<?> type, Object value);

    <QL> ResultIterator<Map<String, Object>> execute(QL query, Map<String, Object> parameters);

    void migrate(Entity entity, TypeSet types, TypeSet targetTypes);

    // properties

    DatastorePropertyManager getDatastorePropertyManager();

}
