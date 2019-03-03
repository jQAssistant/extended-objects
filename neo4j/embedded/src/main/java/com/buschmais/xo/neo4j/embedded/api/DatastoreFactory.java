package com.buschmais.xo.neo4j.embedded.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

import com.buschmais.xo.spi.datastore.Datastore;

interface DatastoreFactory<DS extends Datastore> {
    DS createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException;
}
