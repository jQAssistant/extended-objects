package com.buschmais.cdo.neo4j.impl.cache;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

    private Map<K, V> cache;

    protected AbstractCache(Map<K, V> cache) {
        this.cache = cache;
    }

    protected Map<K,V> getCache() {
        return cache;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Collection<V> values() {
        return cache.values();
    }
}
