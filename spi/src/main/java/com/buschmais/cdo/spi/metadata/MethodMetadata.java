package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

public interface MethodMetadata<Method extends AnnotatedMethod, DatastoreMetadata> {

    Method getAnnotatedMethod();

    DatastoreMetadata getDatastoreMetadata();
}
