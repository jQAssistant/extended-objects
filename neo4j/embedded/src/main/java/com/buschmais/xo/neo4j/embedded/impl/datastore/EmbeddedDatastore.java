package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedDatastore extends AbstractEmbeddedDatastore {

    public EmbeddedDatastore(GraphDatabaseService graphDatabaseService) {
        super(graphDatabaseService);
    }

    @Override
    public void close() {
        graphDatabaseService.shutdown();
    }
}
