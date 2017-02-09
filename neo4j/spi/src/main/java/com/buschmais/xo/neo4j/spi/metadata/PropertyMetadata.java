package com.buschmais.xo.neo4j.spi.metadata;

public class PropertyMetadata {

    private final String name;

    public PropertyMetadata(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
