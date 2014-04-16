package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.spi.datastore.Datastore;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

interface DatastoreFactory<DS extends Datastore> {
    DS createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException;
}

