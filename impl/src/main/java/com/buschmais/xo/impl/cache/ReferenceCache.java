package com.buschmais.xo.impl.cache;

import com.google.common.cache.CacheBuilder;

public class ReferenceCache<Id> extends AbstractCache<Id, Object> {

    protected ReferenceCache() {
        super(CacheBuilder.newBuilder().build());
    }

    @Override
    public void afterCompletion(boolean success) {
    }
}
