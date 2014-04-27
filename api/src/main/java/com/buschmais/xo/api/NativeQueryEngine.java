package com.buschmais.xo.api;

import java.util.Map;

public interface NativeQueryEngine<Q extends NativeQuery<?>> {

    ResultIterator<Map<String, Object>> execute(Q query, Map<String, Object> parameters);

}
