package com.buschmais.cdo.neo4j.impl.cache;

import java.util.WeakHashMap;

public class ReferenceCache<Id> extends AbstractCache<Id, Object> {

    protected ReferenceCache() {
        super(new WeakHashMap<Id, Object>());
    }

    @Override
    public void afterCompletion(boolean success) {
    }
}
