package com.buschmais.xo.neo4j.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.buschmais.xo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;

public class FileDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(uri.toURL().getPath());
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}
