package com.buschmais.xo.neo4j.embedded.impl.model;

import com.buschmais.xo.neo4j.api.model.Neo4jPropertyContainer;

public interface EmbeddedNeo4jPropertyContainer extends Neo4jPropertyContainer {

    void setProperty(String key, Object value);

    Object removeProperty(String key);
}
