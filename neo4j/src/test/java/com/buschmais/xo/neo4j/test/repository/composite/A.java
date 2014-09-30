package com.buschmais.xo.neo4j.test.repository.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    String getName();

    void setName(String name);

}
