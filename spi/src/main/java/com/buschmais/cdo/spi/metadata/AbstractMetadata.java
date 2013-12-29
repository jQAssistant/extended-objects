package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public abstract class AbstractMetadata<S extends AbstractMetadata, DatastoreMetadata> {

    private final AnnotatedType annotatedType;

    private final Collection<AbstractMethodMetadata> properties;

    private final Collection<S> superTypes;

    private final DatastoreMetadata datastoreMetadata;

    protected AbstractMetadata(AnnotatedType annotatedType, Collection<S> superTypes, Collection<AbstractMethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        this.annotatedType = annotatedType;
        this.superTypes = superTypes;
        this.properties = properties;
        this.datastoreMetadata = datastoreMetadata;
    }

    public AnnotatedType getAnnotatedType() {
        return annotatedType;
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

    @Override
    public String toString() {
        return "AbstractMetadata{" +
                "type=" + annotatedType +
                '}';
    }

}
