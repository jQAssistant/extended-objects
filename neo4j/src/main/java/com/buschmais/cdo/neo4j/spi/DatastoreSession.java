package com.buschmais.cdo.neo4j.spi;

import com.buschmais.cdo.api.ResultIterator;

import java.util.List;
import java.util.Map;

public interface DatastoreSession<I, E> {

    void begin();

    void commit();

    void rollback();

    I getId(E entity);

    E create(List<Class<?>> types);

    ResultIterator<E> find(Class<?> type, Object value);

    ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters);

    void migrate(E entity, List<Class<?>> types, List<Class<?>> targetTypes);

    List<Class<?>> getTypes(E entity);
}
