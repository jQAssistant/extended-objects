package com.buschmais.xo.neo4j.embedded.api;

import java.nio.file.Path;
import java.util.Map;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.logging.LogProvider;
import org.neo4j.test.TestDatabaseManagementServiceBuilder;

public class MemoryDatabaseManagementServiceFactory extends AbstractEmbeddedDatabaseManagementServiceFactory {

    @Override
    protected DatabaseManagementService getDatabaseManagementService(Path directory, Map<Setting<?>, Object> settings, LogProvider logProvider) {
        TestDatabaseManagementServiceBuilder databaseManagementServiceBuilder = new TestDatabaseManagementServiceBuilder(directory);
        databaseManagementServiceBuilder.setConfig(settings);
        databaseManagementServiceBuilder.setUserLogProvider(logProvider);
        return databaseManagementServiceBuilder.impermanent()
            .build();
    }

}
