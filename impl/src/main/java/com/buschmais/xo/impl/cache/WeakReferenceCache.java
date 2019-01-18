package com.buschmais.xo.impl.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

public class WeakReferenceCache<Id> extends AbstractCache<Id, Object> {

    protected WeakReferenceCache() {
        super(Caffeine.newBuilder().weakValues().build());
    }

}
