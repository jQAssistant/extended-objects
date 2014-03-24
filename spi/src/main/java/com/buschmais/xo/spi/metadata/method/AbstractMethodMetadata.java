package com.buschmais.xo.spi.metadata.method;

import com.buschmais.xo.spi.reflection.AnnotatedMethod;

public abstract class AbstractMethodMetadata<Method extends AnnotatedMethod, DatastoreMetadata> implements MethodMetadata<Method, DatastoreMetadata> {

    private final Method annotatedMethod;

    private final DatastoreMetadata datastoreMetadata;

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
