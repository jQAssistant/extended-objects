package com.buschmais.xo.impl.cache;

import java.util.WeakHashMap;

public class WeakReferenceCache<Id> extends AbstractCache<Id, Object> {

    protected WeakReferenceCache() {
        super(new WeakHashMap<Id, Object>());
    }

    @Override
    public void afterCompletion(boolean success) {
    }
}
