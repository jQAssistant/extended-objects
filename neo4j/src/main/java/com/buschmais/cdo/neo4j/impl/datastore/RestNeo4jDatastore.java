package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.spi.metadata.MetadataProvider;
import org.neo4j.rest.graphdb.RestGraphDatabase;

public class RestNeo4jDatastore extends  AbstractNeo4jDatastore<RestNeo4jDatastoreSession> {

    private String url;

    public RestNeo4jDatastore(String url) {
        this.url = url;
    }

    @Override
    public void init(MetadataProvider metadataProvider) {
    }

    @Override
    public RestNeo4jDatastoreSession createSession(MetadataProvider metadataProvider) {
        return new RestNeo4jDatastoreSession(new RestGraphDatabase(url), metadataProvider);
    }

    @Override
    public void close() {
    }

}
