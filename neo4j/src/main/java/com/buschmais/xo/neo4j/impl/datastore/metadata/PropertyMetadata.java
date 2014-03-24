package com.buschmais.xo.neo4j.impl.datastore.metadata;

public class PropertyMetadata {

    private final String name;

    public PropertyMetadata(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
