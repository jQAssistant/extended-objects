package com.buschmais.xo.neo4j.api;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.buschmais.xo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;

public class FileDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        String path;
        try {
            path = URLDecoder.decode(uri.toURL().getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MalformedURLException(e.getMessage());
        }
        GraphDatabaseBuilder databaseBuilder = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(path);
        Properties neo4jProperties = Neo4jPropertyHelper.getNeo4jProperties(properties);
        for (String name : neo4jProperties.stringPropertyNames()) {
            databaseBuilder.setConfig(name, neo4jProperties.getProperty(name));
        }
        GraphDatabaseService graphDatabaseService = databaseBuilder.newGraphDatabase();
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}
