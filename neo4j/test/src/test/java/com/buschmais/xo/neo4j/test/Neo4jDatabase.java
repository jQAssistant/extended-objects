package com.buschmais.xo.neo4j.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jXOProvider;
import com.buschmais.xo.neo4j.remote.api.Neo4jRemoteStoreProvider;
import com.buschmais.xo.test.AbstractXOManagerTest;

/**
 * Defines the databases under test for Neo4j.
 */
public enum Neo4jDatabase implements AbstractXOManagerTest.Database {

    MEMORY("memory:///") {
        @Override
        public Class<?> getProvider() {
            return EmbeddedNeo4jXOProvider.class;
        }
    },
    BOLT("bolt://localhost:5001") {
        @Override
        public Class<?> getProvider() {
            return Neo4jRemoteStoreProvider.class;
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

    @Override
    public Map<String, Object> getProperties() {
        return Collections.emptyMap();
    }

}
