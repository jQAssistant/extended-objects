package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.neo4j.impl.datastore.RestNeo4jDatastore;

import java.net.MalformedURLException;
import java.net.URI;

class RemoteDatastoreFactory implements DatastoreFactory<RestNeo4jDatastore> {

    @Override public RestNeo4jDatastore createGraphDatabaseService(URI uri) throws MalformedURLException {
        return new RestNeo4jDatastore(uri.toURL().toExternalForm());
    }
}
