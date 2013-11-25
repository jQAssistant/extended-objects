package com.buschmais.cdo.neo4j.impl.cache;

import java.util.HashMap;

public class TransactionalCache<I> extends AbstractCache<I, Object> {

    private ReferenceCache referenceCache;

    public TransactionalCache() {
        super(new HashMap<I, Object>());
        this.referenceCache = new ReferenceCache();
    }

    @Override
    public void put(I key, Object value) {
        super.put(key, value);
        referenceCache.put(key, value);
    }

    @Override
    public Object get(I key) {
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
    public void remove(I key) {
        super.remove(key);
        referenceCache.remove(key);
    }

    @Override
    public void afterCompletion(boolean success) {
        super.clear();
    }
}
