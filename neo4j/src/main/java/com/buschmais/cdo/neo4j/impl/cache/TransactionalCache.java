package com.buschmais.cdo.neo4j.impl.cache;

import java.util.HashMap;

public class TransactionalCache extends AbstractCache<Long, Object> {

    private ReferenceCache referenceCache;

    public TransactionalCache() {
        super(new HashMap<Long, Object>());
        this.referenceCache = new ReferenceCache();
    }

    @Override
    public void put(Long key, Object value) {
        super.put(key, value);
        referenceCache.put(key, value);
    }

    @Override
    public Object get(Long key) {
        Object value = super.get(key);
        if (value == null) {
            value = referenceCache.get(key);
            if (value != null) {
                super.put(key, value);
            }
        }
        return value;
    }

    @Override
    public void remove(Long key) {
        super.remove(key);
        referenceCache.remove(key);
    }

    @Override
    public void afterCompletion(boolean success) {
        super.clear();
    }
}
