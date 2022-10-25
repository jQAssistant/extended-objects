package com.buschmais.xo.neo4j.embedded.api;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

import java.net.URI;
import java.util.ListIterator;
import java.util.Properties;

import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastore;

import org.neo4j.configuration.GraphDatabaseInternalSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestDatabaseManagementServiceBuilder;

public class MemoryDatastoreFactory implements DatastoreFactory<EmbeddedDatastore> {

    @Override
    public EmbeddedDatastore createGraphDatabaseService(URI uri, Properties properties) {
        DatabaseManagementService managementService = new TestDatabaseManagementServiceBuilder().impermanent()
            .setConfig(GraphDatabaseInternalSettings.track_cursor_close, false)
            .build();
        GraphDatabaseService graphDatabaseService = managementService.database(DEFAULT_DATABASE_NAME);
        return new EmbeddedDatastore(managementService, graphDatabaseService);
    }
}
