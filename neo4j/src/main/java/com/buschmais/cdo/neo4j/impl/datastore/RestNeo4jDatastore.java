package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.spi.Datastore;
import org.neo4j.rest.graphdb.RestGraphDatabase;

public class RestNeo4jDatastore implements Datastore<RestNeo4jDatastoreSession> {

    private String url;
    private NodeMetadataProvider metadataProvider;

    public RestNeo4jDatastore(String url, NodeMetadataProvider metadataProvider) {
        this.url = url;
        this.metadataProvider = metadataProvider;
    }

    @Override
    public RestNeo4jDatastoreSession createSession() {
        return new RestNeo4jDatastoreSession(new RestGraphDatabase(url), metadataProvider);
    }

    @Override
    public void close() {
    }
}
