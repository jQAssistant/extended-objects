package com.buschmais.cdo.impl.cache;

import java.util.HashMap;

public class TransactionalCache<Id> extends AbstractCache<Id, Object> {

    private final ReferenceCache referenceCache;

    public TransactionalCache() {
        super(new HashMap<Id, Object>());
        this.referenceCache = new ReferenceCache();
    }

    @Override
    public void put(Id key, Object value) {
        super.put(key, value);
        referenceCache.put(key, value);
    }

    @Override
    public Object get(Id key) {
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
    public void remove(Id key) {
        super.remove(key);
        referenceCache.remove(key);
    }

    @Override
    public void afterCompletion(boolean success) {
        super.clear();
    }
}
