package com.buschmais.cdo.neo4j.impl;

import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.neo4j.impl.datastore.RestNeo4jDatastore;
import com.buschmais.cdo.neo4j.impl.node.metadata.MetadataProvider;

import java.net.URL;

public class RestNeo4jCdoManagerFactoryImpl extends AbstractNeo4jCdoManagerFactoryImpl<RestNeo4jDatastore> {

    public RestNeo4jCdoManagerFactoryImpl(CdoUnit cdoUnit) {
        super(cdoUnit);
    }

    protected RestNeo4jDatastore createDatastore(URL url) {
        return new RestNeo4jDatastore(url.toExternalForm());
    }

    @Override
    protected void init(RestNeo4jDatastore datastore, MetadataProvider metadataProvider) {
    }
}
