package com.buschmais.cdo.neo4j.impl;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.net.URL;

public class EmbeddedNeo4jCdoManagerFactoryImpl extends AbstractNeo4jCdoManagerFactoryImpl {

    public EmbeddedNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        super(url, entities);
    }

    protected GraphDatabaseService createGraphDatabaseService(URL url) {
        return new GraphDatabaseFactory().newEmbeddedDatabase(url.getPath());
    }
}
