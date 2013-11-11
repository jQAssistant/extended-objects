package com.buschmais.cdo.neo4j.impl.cache;

import org.apache.commons.collections.map.LRUMap;
import scala.util.parsing.combinator.PackratParsers;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NonTransactionalIndexCache extends AbstractIndexCache {

    public NonTransactionalIndexCache() {
        super(null);
    }

    @Override
    protected Map<Object, List<Long>> createTypeCache(Class<?> type) {
        return new LRUMap(4096);
    }
}
