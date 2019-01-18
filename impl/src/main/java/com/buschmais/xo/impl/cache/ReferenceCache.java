package com.buschmais.xo.impl.cache;

import com.github.benmanes.caffeine.cache.Caffeine;

public class ReferenceCache<Id> extends AbstractCache<Id, Object> {

    protected ReferenceCache() {
        super(Caffeine.newBuilder().build());
    }

}
