package com.buschmais.cdo.neo4j.impl.datastore.metadata;

public class IndexedPropertyMetadata {

    private boolean create;

    public IndexedPropertyMetadata(boolean create) {
        this.create = create;
    }

    public boolean isCreate() {
        return create;
    }
}
