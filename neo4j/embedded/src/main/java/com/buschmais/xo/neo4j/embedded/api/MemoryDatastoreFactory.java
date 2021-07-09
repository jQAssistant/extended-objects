package com.buschmais.xo.neo4j.embedded.api;

import java.net.URI;
import java.util.Properties;

import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastore;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

public class MemoryDatastoreFactory implements DatastoreFactory<EmbeddedDatastore> {

    @Override
    public EmbeddedDatastore createGraphDatabaseService(URI uri, Properties properties) {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        return new EmbeddedDatastore(graphDatabaseService);
    }
}
