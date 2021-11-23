package com.buschmais.xo.neo4j.embedded.impl.datastore;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedDatastore extends AbstractEmbeddedDatastore {

    private DatabaseManagementService managementService;

    public EmbeddedNeo4jDatastore(DatabaseManagementService managementService, GraphDatabaseService graphDatabaseService) {
        super(graphDatabaseService);
        this.managementService = managementService;
    }

    @Override
    public void close() {
        managementService.shutdown();
    }

    public DatabaseManagementService getManagementService() {
        return managementService;
    }
}
