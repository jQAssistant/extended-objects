package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.TypeMethod;

public abstract class AbstractMethodMetadata<B extends TypeMethod, DatastoreMetadata> {

    private B beanMethod;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMethodMetadata(B beanMethod, DatastoreMetadata datastoreMetadata) {
        this.beanMethod = beanMethod;
        this.datastoreMetadata = datastoreMetadata;
    }

    public B getBeanMethod() {
        return beanMethod;
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "beanMethod=" + beanMethod + '}';
    }
}
