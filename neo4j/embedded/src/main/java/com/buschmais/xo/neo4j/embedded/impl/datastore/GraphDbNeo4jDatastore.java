package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.graphdb.GraphDatabaseService;

public class GraphDbNeo4jDatastore extends AbstractEmbeddedDatastore {

    public GraphDbNeo4jDatastore(GraphDatabaseService graphDatabaseService) {
        super(graphDatabaseService);
    }

    @Override
    public void close() {
        // externally managed
    }
}
