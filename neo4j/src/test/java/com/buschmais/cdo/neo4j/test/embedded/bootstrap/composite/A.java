package com.buschmais.cdo.neo4j.test.embedded.bootstrap.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    String getName();

    void setName(String name);

}
