package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

public interface TypeMetadata<DatastoreMetadata> {

    AnnotatedType getAnnotatedType();

    Collection<TypeMetadata<?>> getSuperTypes();

    Collection<MethodMetadata> getProperties();

    DatastoreMetadata getDatastoreMetadata();
}
