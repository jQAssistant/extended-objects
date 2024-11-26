package com.buschmais.xo.neo4j.embedded.api;

import java.nio.file.Path;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.logging.LogProvider;

@Slf4j
public class FileDatabaseManagementServiceFactory extends AbstractEmbeddedDatabaseManagementServiceFactory {

    @Override
    protected DatabaseManagementService getDatabaseManagementService(Path directory, Map<Setting<?>, Object> settings, LogProvider instance) {
        DatabaseManagementServiceBuilder databaseManagementServiceBuilder = new DatabaseManagementServiceBuilder(directory);
        databaseManagementServiceBuilder.setConfig(settings);
        databaseManagementServiceBuilder.setUserLogProvider(instance);
        return databaseManagementServiceBuilder.build();
    }

}
