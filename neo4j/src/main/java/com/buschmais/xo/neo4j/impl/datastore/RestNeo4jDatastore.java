package com.buschmais.xo.neo4j.impl.datastore;

import java.util.Collection;

import org.neo4j.rest.graphdb.RestGraphDatabase;

import com.buschmais.xo.spi.metadata.type.TypeMetadata;

public class RestNeo4jDatastore extends AbstractNeo4jDatastore<RestNeo4jDatastoreSession> {

    private final String url;

    public RestNeo4jDatastore(String url) {
        this.url = url;
    }

    @Override
    public void init(Collection<TypeMetadata> registeredMetadata) {
    }

    @Override
    public RestNeo4jDatastoreSession createSession() {
        return new RestNeo4jDatastoreSession(new RestGraphDatabase(url));
    }

    @Override
    public void close() {
    }

}
