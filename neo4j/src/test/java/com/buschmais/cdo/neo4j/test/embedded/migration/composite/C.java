package com.buschmais.cdo.neo4j.test.embedded.migration.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("B")
public interface C {

    String getName();

    void setName(String name);

}
