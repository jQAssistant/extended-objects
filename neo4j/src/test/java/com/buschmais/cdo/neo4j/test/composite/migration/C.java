package com.buschmais.cdo.neo4j.test.composite.migration;

import com.buschmais.cdo.neo4j.annotation.Label;

@Label("C")
public interface C {

    String getName();

    void setName(String name);

}
