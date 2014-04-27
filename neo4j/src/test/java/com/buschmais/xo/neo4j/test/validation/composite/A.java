package com.buschmais.xo.neo4j.test.validation.composite;

import javax.validation.constraints.NotNull;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    @NotNull
    @Indexed
    String getName();

    void setName(String name);

    @NotNull
    B getB();

    void setB(B b);
}
