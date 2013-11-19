package com.buschmais.cdo.neo4j.impl.query;

import com.buschmais.cdo.api.IterableResult;

import java.util.Iterator;
import java.util.Map;

public interface QueryExecutor {

    Iterator<Map<String, Object>> execute(String query, Map<String, Object> parameters);

}
