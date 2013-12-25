package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.net.MalformedURLException;
import java.net.URI;

public class FileDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri) throws MalformedURLException {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(uri.toURL().getPath());
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}
