package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public abstract class AbstractMetadata<S extends AbstractMetadata, DatastoreMetadata> {

    private AnnotatedType annotatedType;

    private Collection<MethodMetadata> properties;

    private Collection<S> superTypes;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMetadata(AnnotatedType annotatedType, Collection<S> superTypes, Collection<MethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
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

    public Collection<MethodMetadata> getProperties() {
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
