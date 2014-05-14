package com.buschmais.xo.spi.datastore;

import com.buschmais.xo.api.ResultIterator;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface DatastoreQuery<QL extends Annotation> {

    ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters);

    ResultIterator<Map<String, Object>> execute(QL query, Map<String, Object> parameters);

}
