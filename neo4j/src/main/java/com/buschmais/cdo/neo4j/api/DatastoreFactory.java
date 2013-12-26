package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.spi.datastore.Datastore;

import java.net.MalformedURLException;
import java.net.URI;

interface DatastoreFactory<DS extends Datastore> {
    DS createGraphDatabaseService(URI uri) throws MalformedURLException;
}

