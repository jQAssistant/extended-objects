package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.neo4j.impl.datastore.RestNeo4jDatastore;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;
import com.buschmais.cdo.neo4j.spi.DatastoreSession;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class RestNeo4jCdoManagerFactoryImpl extends AbstractNeo4jCdoManagerFactoryImpl<RestNeo4jDatastore> {

    public RestNeo4jCdoManagerFactoryImpl(URL url, Class<?>... entities) {
        super(url, entities);
    }

    protected RestNeo4jDatastore createDatastore(URL url, NodeMetadataProvider metadataProvider) {
        return new RestNeo4jDatastore(url.toExternalForm(), metadataProvider);
    }

    @Override
    protected void init(RestNeo4jDatastore datastore, NodeMetadataProvider nodeMetadataProvider) {
    }
}
