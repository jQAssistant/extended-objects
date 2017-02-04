package com.buschmais.xo.neo4j.embedded.test.bootstrap.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    @Indexed
    String getName();

    void setName(String name);
}
