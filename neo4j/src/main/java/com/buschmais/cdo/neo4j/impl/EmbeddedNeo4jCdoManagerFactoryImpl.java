package com.buschmais.cdo.neo4j.impl;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.net.URL;

public class EmbeddedNeo4jCdoManagerFactoryImpl extends AbstractNeo4jCdoManagerFactoryImpl {

    private GraphDatabaseService graphDatabaseService;

    public EmbeddedNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        super(url, entities);
        graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(url.getPath());
    }

    protected GraphDatabaseService getGraphDatabaseService(URL url) {
        return graphDatabaseService;
    }

    @Override
    public void close() {
        graphDatabaseService.shutdown();
    }
}
