package com.buschmais.xo.impl.cache;

import java.util.Collection;

/**
 * Abstract cache implementation.
 *
 * @param <Key>   The key type.
 * @param <Value> The value type.
 */
public abstract class AbstractCache<Key, Value> implements Cache<Key, Value> {

    private final com.google.common.cache.Cache<Key, Value> cache;

    /**
     * Constructor.
     *
     * @param cache The map to use as cache.
     */
    protected AbstractCache(com.google.common.cache.Cache<Key, Value> cache) {
        this.cache = cache;
    }

    @Override
    public void put(Key key, Value value) {
        cache.put(key, value);
    }

    @Override
    public Value get(Key key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void remove(Key key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public Collection<Value> values() {
        return cache.asMap().values();
    }
}
