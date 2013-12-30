package com.buschmais.cdo.spi.metadata.method;

import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

public abstract class AbstractMethodMetadata<Method extends AnnotatedMethod, DatastoreMetadata> implements MethodMetadata<Method, DatastoreMetadata> {

    private Method annotatedMethod;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMethodMetadata(Method annotatedMethod, DatastoreMetadata datastoreMetadata) {
        this.annotatedMethod = annotatedMethod;
        this.datastoreMetadata = datastoreMetadata;
    }

    @Override
    public Method getAnnotatedMethod() {
        return annotatedMethod;
    }

    @Override
    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "annotatedMethod=" + annotatedMethod + '}';
    }
}
