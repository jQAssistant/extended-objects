package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.AnnotatedMethod;

public abstract class AbstractMethodMetadata<Method extends AnnotatedMethod, DatastoreMetadata> implements MethodMetadata<Method, DatastoreMetadata> {

    private Method annotateddMethod;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMethodMetadata(Method annotatedMethod, DatastoreMetadata datastoreMetadata) {
        this.annotateddMethod = annotatedMethod;
        this.datastoreMetadata = datastoreMetadata;
    }

    @Override
    public Method getAnnotatedMethod() {
        return annotateddMethod;
    }

    @Override
    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "annotateddMethod=" + annotateddMethod + '}';
    }
}
