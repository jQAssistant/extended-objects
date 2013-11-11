package com.buschmais.cdo.neo4j.impl.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransactionalIndexCache extends AbstractIndexCache {

    public TransactionalIndexCache(NonTransactionalIndexCache nonTransactionalIndexCache) {
        super(nonTransactionalIndexCache);
    }

    @Override
    protected Map<Object, List<Long>> createTypeCache(Class<?> type) {
        return new HashMap<>();
    }
}
