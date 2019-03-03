package com.buschmais.xo.neo4j.embedded.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Properties;

import com.buschmais.xo.neo4j.embedded.impl.datastore.EmbeddedNeo4jDatastore;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
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
        LOGGER.debug("Creating graph database service datastore for directory '{}'.", storeDir.getAbsolutePath());
        GraphDatabaseBuilder databaseBuilder = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(storeDir);
        Properties neo4jProperties = Neo4jPropertyHelper.getNeo4jProperties(properties);
        for (String name : neo4jProperties.stringPropertyNames()) {
            databaseBuilder.setConfig(name, neo4jProperties.getProperty(name));
        }
        GraphDatabaseService graphDatabaseService = databaseBuilder.newGraphDatabase();
        LOGGER.debug("Graph database service for directory '{}' created.", storeDir.getAbsolutePath());
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}
