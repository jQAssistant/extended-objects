package com.buschmais.cdo.neo4j.api;

import com.buschmais.cdo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.net.URI;

public class MemoryDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri) {
        GraphDatabaseService graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}