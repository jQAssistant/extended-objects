package com.buschmais.cdo.neo4j.test.embedded.validation.composite;

import com.buschmais.cdo.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;

import javax.validation.constraints.NotNull;

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
