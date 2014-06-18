package com.buschmais.xo.neo4j.api;

import com.buschmais.xo.neo4j.impl.datastore.EmbeddedNeo4jDatastore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Properties;

public class FileDatastoreFactory implements DatastoreFactory<EmbeddedNeo4jDatastore> {

    @Override
    public EmbeddedNeo4jDatastore createGraphDatabaseService(URI uri, Properties properties) throws MalformedURLException {
        GraphDatabaseBuilder databaseBuilder = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(uri.toURL().getPath());
        Properties neo4jProperties = Neo4jPropertyHelper.getNeo4jProperties(properties);
        for (String name : neo4jProperties.stringPropertyNames()) {
            databaseBuilder.setConfig(name, neo4jProperties.getProperty(name));
        }
        GraphDatabaseService graphDatabaseService = databaseBuilder.newGraphDatabase();
        return new EmbeddedNeo4jDatastore(graphDatabaseService);
    }
}
