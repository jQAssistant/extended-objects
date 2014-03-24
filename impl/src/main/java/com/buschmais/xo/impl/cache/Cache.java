package com.buschmais.xo.impl.cache;

import java.util.Collection;

/**
 * Defines the cache interface.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    void remove(K key);

    Collection<V> values();

    void clear();

    void afterCompletion(boolean success);
}
