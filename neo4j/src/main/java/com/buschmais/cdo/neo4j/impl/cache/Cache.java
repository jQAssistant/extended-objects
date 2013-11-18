package com.buschmais.cdo.neo4j.impl.cache;

import java.util.Collection;

public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    void remove(K key);

    Collection<V> values();

    void clear();

    void afterCompletion(boolean success);
}
