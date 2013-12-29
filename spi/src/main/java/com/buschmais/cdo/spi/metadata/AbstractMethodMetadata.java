package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

public abstract class AbstractMethodMetadata<B extends AnnotatedMethod, DatastoreMetadata> {

    private B annotatedMethod;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMethodMetadata(B annotatedMethod, DatastoreMetadata datastoreMetadata) {
        this.annotatedMethod = annotatedMethod;
        this.datastoreMetadata = datastoreMetadata;
    }

    public B getAnnotatedMethod() {
        return annotatedMethod;
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "annotatedMethod=" + annotatedMethod + '}';
    }
}
