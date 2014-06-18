package com.buschmais.xo.impl.cache;

import com.google.common.cache.CacheBuilder;

public class WeakReferenceCache<Id> extends AbstractCache<Id, Object> {

    protected WeakReferenceCache() {
        super(CacheBuilder.newBuilder().weakValues().build());
    }

    @Override
    public void afterCompletion(boolean success) {
    }
}
