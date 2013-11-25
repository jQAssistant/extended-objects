package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.neo4j.impl.datastore.RestNeo4jDatastore;
import com.buschmais.cdo.neo4j.impl.node.metadata.NodeMetadataProvider;

import java.net.URL;

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
