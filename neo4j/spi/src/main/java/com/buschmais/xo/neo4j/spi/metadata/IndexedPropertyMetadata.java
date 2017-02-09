package com.buschmais.xo.neo4j.spi.metadata;

public class IndexedPropertyMetadata {

    private final boolean create;
    private final boolean unique;

    public IndexedPropertyMetadata(boolean create, boolean unique) {
        this.create = create;
        this.unique = unique;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isCreate() {
        return create;
    }
}
