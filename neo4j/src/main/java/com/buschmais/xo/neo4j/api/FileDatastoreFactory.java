package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.net.MalformedURLException;
import java.net.URI;

public class FileDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri) throws MalformedURLException {
        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(uri.toURL().getPath());
        registerShutdownHook(graphDatabaseService);
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }

    private void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it shuts down
        // nicely when the VM exits (even if you "Ctrl-C" the running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
