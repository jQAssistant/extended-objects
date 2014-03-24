package com.buschmais.xo.inject.test.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    @Indexed
    public String getValue();

    public void setValue(String value);
}
