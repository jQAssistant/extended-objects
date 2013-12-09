package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.node.metadata.MetadataProvider;
import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedNeo4jDatastore extends AbstractNeo4jDatastore<EmbeddedNeo4jDatastoreSession> {

    private GraphDatabaseService graphDatabaseService;

    public EmbeddedNeo4jDatastore(GraphDatabaseService graphDatabaseService) {
        this.graphDatabaseService = graphDatabaseService;
    }

    @Override
    public EmbeddedNeo4jDatastoreSession createSession(MetadataProvider metadataProvider) {
        return new EmbeddedNeo4jDatastoreSession(graphDatabaseService, metadataProvider);
    }

    @Override
    public void close() {
        graphDatabaseService.shutdown();
    }
}
