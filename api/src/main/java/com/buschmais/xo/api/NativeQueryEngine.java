package com.buschmais.xo.api;

import java.util.Map;

public interface NativeQueryEngine<T> {

    ResultIterator<Map<String, Object>> execute(T query, Map<String, Object> parameters);

}
