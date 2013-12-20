package com.buschmais.cdo.spi.metadata;

import com.buschmais.cdo.spi.reflection.TypeMethod;

public abstract class AbstractMethodMetadata<B extends TypeMethod, DatastoreMetadata> {

    private B typeMethod;

    private DatastoreMetadata datastoreMetadata;

    protected AbstractMethodMetadata(B typeMethod, DatastoreMetadata datastoreMetadata) {
        this.typeMethod = typeMethod;
        this.datastoreMetadata = datastoreMetadata;
    }

    public B getTypeMethod() {
        return typeMethod;
    }

    public DatastoreMetadata getDatastoreMetadata() {
        return datastoreMetadata;
    }

    @Override
    public String toString() {
        return "AbstractMethodMetadata{" + "typeMethod=" + typeMethod + '}';
    }
}
