package com.buschmais.cdo.neo4j.spi;

import com.buschmais.cdo.api.ResultIterator;

import java.util.Map;

public interface DatastoreSession<I, E> {

    void begin();

    void commit();

    void rollback();

    I getId(E entity);

    E create(TypeSet types);

    ResultIterator<E> find(Class<?> type, Object value);

    ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters);

    void migrate(E entity, TypeSet types, TypeSet targetTypes);

    TypeSet getTypes(E entity);

}
