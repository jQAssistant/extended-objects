package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

public abstract class AbstractMethodMetadata<B extends AnnotatedMethod, DatastoreMetadata> {

    private B annotateddMethod;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMethodMetadata(B annotatedMethod, DatastoreMetadata datastoreMetadata) {
        this.annotateddMethod = annotatedMethod;
        this.datastoreMetadata = datastoreMetadata;
    }

    public B getAnnotateddMethod() {
        return annotateddMethod;
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "annotateddMethod=" + annotateddMethod + '}';
    }
}
