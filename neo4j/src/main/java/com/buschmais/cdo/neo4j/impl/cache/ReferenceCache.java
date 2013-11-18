package com.buschmais.cdo.neo4j.impl.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ReferenceCache extends AbstractCache<Long, Object> {

    protected ReferenceCache() {
        super(new WeakHashMap<Long, Object>());
    }

    @Override
    public void afterCompletion(boolean success) {
    }
}
