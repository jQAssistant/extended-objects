package com.buschmais.xo.spi.datastore;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.buschmais.xo.api.ResultIterator;

/**
 * Defines an executable datastore query.
 *
 * @param <QL>
 *     The query language type.
 */
public interface DatastoreQuery<QL extends Annotation> {

    /**
     * Execute the query using a string expression.
     *
     * @param query
     *     The expression.
     * @param parameters
     *     The parameters.
     * @return The {@link com.buschmais.xo.api.ResultIterator}, each entry holding a
     * map of a column alias and its value in datastore representation.
     */
    ResultIterator<Map<String, Object>> execute(String query, Map<String, Object> parameters);

    /**
     * Execute the query using an annotation expression.
     *
     * @param query
     *     The expression.
     * @param parameters
     *     The parameters.
     * @return The {@link com.buschmais.xo.api.ResultIterator}, each entry holding a
     * map of a column alias and its value in datastore representation.
     */
    ResultIterator<Map<String, Object>> execute(QL query, Map<String, Object> parameters);

}
