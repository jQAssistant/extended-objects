package com.buschmais.xo.neo4j.test.migration.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("C")
public interface C {

    String getName();

    void setName(String name);

}
