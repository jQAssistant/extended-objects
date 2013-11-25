package com.buschmais.cdo.neo4j.spi;

import org.neo4j.graphdb.Node;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface DatastoreSession<E> {

    void begin();

    void commit();

    void rollback();

    E create(List<Class<?>> types);

    Iterator<E> find(Class<?> type, Object value);

    Iterator<Map<String, Object>> execute(String query, Map<String, Object> parameters);

    void migrate(E entity, List<Class<?>> types, List<Class<?>> targetTypes);

}
