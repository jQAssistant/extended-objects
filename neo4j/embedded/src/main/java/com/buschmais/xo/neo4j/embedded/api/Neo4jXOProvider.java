package com.buschmais.xo.neo4j.embedded.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class Neo4jXOProvider extends EmbeddedNeo4jXOProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jXOProvider.class);

    public Neo4jXOProvider() {
        LOGGER.warn("The provider " + Neo4jXOProvider.class.getName() + " is deprecated and has been replaced by " + EmbeddedNeo4jXOProvider.class.getName());
    }

}
