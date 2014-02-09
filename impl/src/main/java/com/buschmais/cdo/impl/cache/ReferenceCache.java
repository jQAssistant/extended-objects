package com.buschmais.cdo.impl.cache;

import java.util.HashMap;

public class ReferenceCache<Id> extends AbstractCache<Id, Object> {

    protected ReferenceCache() {
        super(new HashMap<Id, Object>());
    }

    @Override
    public void afterCompletion(boolean success) {
    }
}
