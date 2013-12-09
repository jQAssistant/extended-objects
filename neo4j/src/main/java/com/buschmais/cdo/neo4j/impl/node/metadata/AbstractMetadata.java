package com.buschmais.cdo.neo4j.impl.node.metadata;

import java.util.Collection;

public abstract class AbstractMetadata<DatastoreMetadata> {

    private Collection<AbstractMethodMetadata> properties;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMetadata(Collection<AbstractMethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        this.properties = properties;
        this.datastoreMetadata = datastoreMetadata;
    }

    public Collection<AbstractMethodMetadata> getProperties() {
        return properties;
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }
}
