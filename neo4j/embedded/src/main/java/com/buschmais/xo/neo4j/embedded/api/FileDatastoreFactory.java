package com.buschmais.xo.neo4j.embedded.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedDatastore;

import org.neo4j.configuration.GraphDatabaseInternalSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class FileDatastoreFactory implements DatastoreFactory<EmbeddedDatastore> {

    private static final Pattern NEO4J_PROPERTY_PATTERN = Pattern.compile("neo4j\\.(.*)");

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDatastoreFactory.class);

    @Override
    public EmbeddedDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        String path;
        try {
            path = URLDecoder.decode(uri.toURL()
                .getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MalformedURLException(e.getMessage());
        }
        File storeDir = new File(path);
        storeDir.mkdirs();
        LOGGER.debug("Creating graph database service datastore for directory '{}'.", storeDir.getAbsolutePath());
        DatabaseManagementServiceBuilder databaseManagementServiceBuilder = new DatabaseManagementServiceBuilder(storeDir.toPath());
        Map<String, String> neo4jProperties = getNeo4jProperties(properties);
        databaseManagementServiceBuilder.setConfigRaw(neo4jProperties);
        databaseManagementServiceBuilder.setConfig(GraphDatabaseInternalSettings.track_cursor_close, false);
        DatabaseManagementService managementService = databaseManagementServiceBuilder.build();
        GraphDatabaseService graphDatabaseService = managementService.database(DEFAULT_DATABASE_NAME);
        LOGGER.debug("Graph database service for directory '{}' created.", storeDir.getAbsolutePath());
        return new EmbeddedDatastore(managementService, graphDatabaseService);
    }

    private Map<String, String> getNeo4jProperties(Properties properties) {
        Map<String, String> neo4jProperties = new HashMap<>();
        for (String propertyName : properties.stringPropertyNames()) {
            Matcher matcher = NEO4J_PROPERTY_PATTERN.matcher(propertyName);
            if (matcher.matches()) {
                String neo4jProperty = matcher.group(1);
                neo4jProperties.put(neo4jProperty, properties.getProperty(propertyName));
            }
        }
        return neo4jProperties;
    }
}
