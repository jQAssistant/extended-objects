package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.reflection.AnnotatedMethod;

public interface MethodMetadata<Method extends AnnotatedMethod, DatastoreMetadata> {

    Method getAnnotatedMethod();

    DatastoreMetadata getDatastoreMetadata();
}
