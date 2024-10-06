package com.buschmais.xo.neo4j.embedded.api;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.neo4j.configuration.Config;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.config.Setting;

interface DatabaseManagementServiceFactory {

    DatabaseManagementService createDatabaseManagementService(URI uri, Config config, Properties properties);

    default Map<Setting<?>, Object> toSettings(Config config) {
        Map<Setting<?>, Object> settings = new HashMap<>();
        for (Setting<?> setting : config.getDeclaredSettings()
            .values()) {
            if (config.isExplicitlySet(setting)) {
                settings.put(setting, config.get(setting));
            }
        }
        return settings;
    }

}
