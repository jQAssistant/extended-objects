package com.buschmais.xo.neo4j.embedded.api;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;

import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedNeo4jDatastore;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDatastoreFactory.class);

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        String path;
        try {
            path = URLDecoder.decode(uri.toURL().getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MalformedURLException(e.getMessage());
        }
        File storeDir = new File(path);
        storeDir.mkdirs();
        LOGGER.debug("Creating graph database service datastore for directory '{}'.", storeDir.getAbsolutePath());
        DatabaseManagementServiceBuilder databaseManagementServiceBuilder = new DatabaseManagementServiceBuilder(storeDir);
        Map<String, String> neo4jProperties = Neo4jPropertyHelper.getNeo4jProperties(properties);
        databaseManagementServiceBuilder.setConfigRaw(neo4jProperties);
        DatabaseManagementService managementService = databaseManagementServiceBuilder.build();
        GraphDatabaseService graphDatabaseService = managementService.database(DEFAULT_DATABASE_NAME);
        LOGGER.debug("Graph database service for directory '{}' created.", storeDir.getAbsolutePath());
        return new EmbeddedNeo4jDatastore(managementService, graphDatabaseService);
    }
}
