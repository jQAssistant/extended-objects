package com.buschmais.cdo.impl.cache;

import java.util.Collection;
import java.util.Map;

/**
 * Abstract cache implementation.
 *
 * @param <Key>   The key type.
 * @param <Value> The value type.
 */
public abstract class AbstractCache<Key, Value> implements Cache<Key, Value> {

    private Map<Key, Value> cache;

    protected AbstractCache(Map<Key, Value> cache) {
        this.cache = cache;
    }

    @Override
    public void put(Key key, Value value) {
        cache.put(key, value);
    }

    @Override
    public Value get(Key key) {
        return cache.get(key);
    }

    @Override
    public void remove(Key key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public Collection<Value> values() {
        return cache.values();
    }
}
