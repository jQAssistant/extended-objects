package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.datastore.metadata.NodeMetadata;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import java.util.Collection;

public class RestNeo4jDatastore extends  AbstractNeo4jDatastore<RestNeo4jDatastoreSession> {

    private String url;

    public RestNeo4jDatastore(String url) {
        this.url = url;
    }

    @Override
    public void init(Collection<TypeMetadata<NodeMetadata>> registeredMetadata) {
    }

    @Override
    public RestNeo4jDatastoreSession createSession() {
        return new RestNeo4jDatastoreSession(new RestGraphDatabase(url));
    }

    @Override
    public void close() {
    }

}
