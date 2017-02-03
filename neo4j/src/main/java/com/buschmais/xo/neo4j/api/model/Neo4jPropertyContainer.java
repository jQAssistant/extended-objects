package com.buschmais.xo.neo4j.api.model;

import java.util.Map;

/**
 * Defines the interface for a property container, i.e. an entity holding key/value pairs.
 */
public interface Neo4jPropertyContainer {

    boolean hasProperty(String key);

    Object getProperty(String key);

    Map<String, Object> getProperties();
}
