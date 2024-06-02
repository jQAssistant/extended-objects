package com.buschmais.xo.neo4j.embedded.api;

import java.net.URI;

import org.neo4j.configuration.Config;
import org.neo4j.configuration.GraphDatabaseInternalSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.test.TestDatabaseManagementServiceBuilder;

public class MemoryDatabaseManagementServiceFactory implements DatabaseManagementServiceFactory {

    @Override
    public DatabaseManagementService createDatabaseManagementService(URI uri, Config config) {
        return new TestDatabaseManagementServiceBuilder().impermanent()
            .setConfig(config)
            .setConfig(GraphDatabaseInternalSettings.track_cursor_close, false)
            .setUserLogProvider(Slf4jLogProvider.INSTANCE)
            .build();
    }

}
