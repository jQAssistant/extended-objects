package com.buschmais.cdo.neo4j.impl.datastore;

import com.buschmais.cdo.spi.datastore.DatastoreMetadataFactory;
import com.buschmais.cdo.spi.metadata.TypeMetadata;
import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreMetadataProvider;

import java.util.*;

public abstract class AbstractNeo4jDatastore<DS extends AbstractNeo4jDatastoreSession> implements Datastore<DS> {

    private Neo4jMetadataFactory metadataFactory = new Neo4jMetadataFactory();

    @Override
    public DatastoreMetadataProvider createMetadataProvider(Collection<TypeMetadata> entityTypes) {
        return new Neo4jMetadataProvider(entityTypes);
    }

    @Override
    public DatastoreMetadataFactory<?> getMetadataFactory() {
        return metadataFactory;
    }

}
