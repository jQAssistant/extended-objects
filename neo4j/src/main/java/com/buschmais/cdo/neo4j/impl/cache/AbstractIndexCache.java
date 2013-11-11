package com.buschmais.cdo.neo4j.impl.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractIndexCache {

    private AbstractIndexCache nextLevelCache;

    private Map<Class<?>, Map<Object, List<Long>>> cache = new HashMap<>();

    protected AbstractIndexCache(AbstractIndexCache nextLevelCache) {
        this.nextLevelCache = nextLevelCache;
    }

    public void put(Class<?> type, Object value, List<Long> ids) {
        Map<Object, List<Long>> typeCache = getTypeCache(type);
        typeCache.put(value, ids);
    }

    public void remove(Class<?> type, Object value) {
        getTypeCache(type).remove(value);
    }

    public List<Long> get(Class<?> type, Object value) {
        Map<Object, List<Long>> typeCache = getTypeCache(type);
        List<Long> ids = typeCache.get(value);
        if (nextLevelCache != null && ids == null) {
            ids = nextLevelCache.get(type, value);
            if (ids != null) {
                typeCache.put(value, ids);
            }
        }
        return ids;
    }

    protected abstract Map<Object, List<Long>> createTypeCache(Class<?> type);

    public void flush() {
        if (nextLevelCache != null) {
            Map<Class<?>, Map<Object, List<Long>>> cache = getCache();
            for (Map.Entry<Class<?>, Map<Object, List<Long>>> cacheEntry : cache.entrySet()) {
                Class<?> type = cacheEntry.getKey();
                Map<Object, List<Long>> typeCache = cacheEntry.getValue();
                for (Map.Entry<Object, List<Long>> typeCacheEntry : typeCache.entrySet()) {
                    Object value = typeCacheEntry.getKey();
                    List<Long> ids = typeCacheEntry.getValue();
                    nextLevelCache.put(type, value, ids);
                }
            }
        }
        cache.clear();
    }

    public void clear() {
        cache.clear();
    }

    protected Map<Class<?>, Map<Object, List<Long>>> getCache() {
        return cache;
    }

    private Map<Object, List<Long>> getTypeCache(Class<?> type) {
        Map<Object, List<Long>> typeCache = cache.get(type);
        if (typeCache == null) {
            typeCache = createTypeCache(type);
            cache.put(type, typeCache);
        }
        return typeCache;
    }
}
