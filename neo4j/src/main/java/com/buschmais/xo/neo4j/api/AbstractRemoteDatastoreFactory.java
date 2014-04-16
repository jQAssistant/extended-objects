package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.neo4j.impl.datastore.RestNeo4jDatastore;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

abstract class AbstractRemoteDatastoreFactory implements DatastoreFactory<RestNeo4jDatastore> {

    @Override
    public RestNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        return new RestNeo4jDatastore(uri.toURL().toExternalForm());
    }
}
