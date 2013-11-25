package com.buschmais.cdo.neo4j.impl.cache;

import java.util.WeakHashMap;

public class ReferenceCache<I> extends AbstractCache<I, Object> {

    protected ReferenceCache() {
        super(new WeakHashMap<I, Object>());
    }

    @Override
    public void afterCompletion(boolean success) {
    }
}
