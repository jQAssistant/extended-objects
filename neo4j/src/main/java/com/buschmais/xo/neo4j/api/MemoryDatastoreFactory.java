package com.buschmais.xo.neo4j.api;

import java.net.URI;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.buschmais.xo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;

public class MemoryDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}