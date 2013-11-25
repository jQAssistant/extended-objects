package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.spi.Datastore;
import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedNeo4jDatastore implements Datastore<EmbeddedNeo4jDatastoreSession> {

    private GraphDatabaseService graphDatabaseService;
    private NodeMetadataProvider metadataProvider;

    public EmbeddedNeo4jDatastore(GraphDatabaseService graphDatabaseService, NodeMetadataProvider metadataProvider) {
        this.graphDatabaseService = graphDatabaseService;
        this.metadataProvider = metadataProvider;
    }

    @Override
    public EmbeddedNeo4jDatastoreSession createSession() {
        return new EmbeddedNeo4jDatastoreSession(graphDatabaseService, metadataProvider);
    }

    @Override
    public void close() {
        graphDatabaseService.shutdown();
    }
}
