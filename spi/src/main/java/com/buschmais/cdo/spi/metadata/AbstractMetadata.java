package com.buschmais.cdo.spi.metadata;

import java.util.Collection;

public abstract class AbstractMetadata<S extends AbstractMetadata, DatastoreMetadata> {

    private Collection<AbstractMethodMetadata> properties;

    private Collection<S> superTypes;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMetadata(Collection<S> superTypes, Collection<AbstractMethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        this.superTypes = superTypes;
        this.properties = properties;
        this.datastoreMetadata = datastoreMetadata;
    }

    public Collection<S> getSuperTypes() {
        return superTypes;
    }

    public Collection<AbstractMethodMetadata> getProperties() {
        return properties;
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }
}
