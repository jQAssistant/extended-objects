package com.buschmais.xo.neo4j.api.model;

import java.util.Map;

/**
 * Defines a property container, i.e. an entity holding key/value pairs.
 */
public interface Neo4jPropertyContainer {

    /**
     * Return the id of the container.
     *
     * @return The id.
     */
    long getId();

    /**
     * Return if the container has a property for a given key.
     *
     * @param key
     *     The property key.
     * @return <code>true</code> if the container has a property.
     */
    boolean hasProperty(String key);

    /**
     * Return the value of a property.
     *
     * @param key
     *     The property key.
     * @return The value.
     */
    Object getProperty(String key);

    /**
     * Return a {@link Map} containing all properties.
     *
     * @return The properties.
     */
    Map<String, Object> getProperties();
}
