package com.buschmais.xo.neo4j.embedded.api;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.buschmais.xo.api.XOException;

import org.neo4j.configuration.Config;
import org.neo4j.configuration.GraphDatabaseInternalSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDatabaseManagementServiceFactory implements DatabaseManagementServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDatabaseManagementServiceFactory.class);

    @Override
    public DatabaseManagementService createDatabaseManagementService(URI uri, Config config) {
        String path;
        try {
            path = URLDecoder.decode(uri.toURL()
                .getPath(), StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            throw new XOException("Cannot get path fro URI" + uri, e);
        }
        File storeDir = new File(path);
        storeDir.mkdirs();
        LOGGER.debug("Creating graph database service datastore for directory '{}'.", storeDir.getAbsolutePath());

        DatabaseManagementServiceBuilder databaseManagementServiceBuilder = new DatabaseManagementServiceBuilder(storeDir.toPath());
        databaseManagementServiceBuilder.setConfig(toSettings(config));
        databaseManagementServiceBuilder.setConfig(GraphDatabaseInternalSettings.track_cursor_close, false);
        databaseManagementServiceBuilder.setUserLogProvider(Slf4jLogProvider.INSTANCE);
        return databaseManagementServiceBuilder.build();
    }
}
