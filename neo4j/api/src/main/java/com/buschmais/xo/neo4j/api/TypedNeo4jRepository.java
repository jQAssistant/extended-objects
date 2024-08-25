package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.api.ResultIterable;

/**
 * Defines the interface for typed Neo4j repositories.
 */
public interface TypedNeo4jRepository<T> {

    /**
     * Find all instances from an indexed property.
     *
     * @param value
     *     The value.
     * @return An {@link Iterable} returning the instances.
     */
    ResultIterable<T> find(Object value);

}
