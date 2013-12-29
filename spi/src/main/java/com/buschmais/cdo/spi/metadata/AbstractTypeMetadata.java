package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public abstract class AbstractTypeMetadata<DatastoreMetadata> implements TypeMetadata<DatastoreMetadata> {

    private AnnotatedType annotatedType;

    private Collection<MethodMetadata> properties;

    private Collection<TypeMetadata<?>> superTypes;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractTypeMetadata(AnnotatedType annotatedType, Collection<TypeMetadata<?>> superTypes, Collection<MethodMetadata> properties, DatastoreMetadata datastoreMetadata) {
        this.annotatedType = annotatedType;
        this.superTypes = superTypes;
        this.properties = properties;
        this.datastoreMetadata = datastoreMetadata;
    }

    @Override
    public AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public Collection<TypeMetadata<?>> getSuperTypes() {
        return superTypes;
    }

    @Override
    public Collection<MethodMetadata> getProperties() {
        return properties;
    }

    @Override
    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractTypeMetadata{" +
                "type=" + annotatedType +
                '}';
    }

}
