package com.buschmais.xo.neo4j.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.neo4j.remote.api.RemoteNeo4jXOProvider;
import com.buschmais.xo.test.AbstractXOManagerIT;

/**
 * Defines the databases under test for Neo4j.
 */
public enum Neo4jDatabase implements AbstractXOManagerIT.Database {

    MEMORY("memory:///") {
        @Override
        public Class<?> getProvider() {
            return EmbeddedNeo4jXOProvider.class;
        }

        @Override
        public Properties getProperties() {
            return new Properties();
        }
    },
    BOLT("bolt://localhost:6001") {
        @Override
        public Class<?> getProvider() {
            return RemoteNeo4jXOProvider.class;
        }

        @Override
        public Properties getProperties() {
            Properties properties = new Properties();
            properties.put("neo4j.remote.statement.log.level", "info");
            return properties;
        }

    };
    private URI uri;

    Neo4jDatabase(String uri) {
        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public URI getUri() {
        return uri;
    }

}
