package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedNeo4jDatastore extends AbstractEmbeddedNeo4jDatastore {

    public EmbeddedNeo4jDatastore(GraphDatabaseService graphDatabaseService) {
        super(graphDatabaseService);
    }

    @Override
    public void close() {
        graphDatabaseService.shutdown();
    }
}
