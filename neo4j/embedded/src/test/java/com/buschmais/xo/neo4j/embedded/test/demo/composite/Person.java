package com.buschmais.xo.neo4j.embedded.test.demo.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Person")
public interface Person {

    @Indexed
    String getName();

    void setName(String name);
}
