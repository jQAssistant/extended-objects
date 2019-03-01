package com.buschmais.xo.trace.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface A {
    String getName();

    void setName(String name);
}
