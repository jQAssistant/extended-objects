package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.api.ResultIterable;

/**
 * Defines the interface for Neo4j repositories.
 */
public interface Neo4jRepository {

    /**
     * Find all instances according to the given type and value from an indexed
     * property.
     *
     * @param <T>
     *     The type.
     * @param type
     *     The type.
     * @param value
     *     The value.
     * @return An {@link Iterable} returning the instances.
     */
    <T> ResultIterable<T> find(Class<T> type, Object value);

}
