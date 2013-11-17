package com.buschmais.cdo.api;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface Query {

    Query withParameter(String name, Object value);

    Query withParameters(Map<String, Object> parameters);

    <T> IterableQueryResult<T> execute();

}
