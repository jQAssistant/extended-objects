package com.buschmais.xo.api.metadata.method;

import com.buschmais.xo.api.metadata.reflection.AnnotatedMethod;

public interface MethodMetadata<Method extends AnnotatedMethod, DatastoreMetadata> {

    Method getAnnotatedMethod();

    DatastoreMetadata getDatastoreMetadata();
}
