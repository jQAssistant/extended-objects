package com.buschmais.xo.neo4j.test.mapping.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("F")
public interface F {

    E2F getE2F();

    void setValue(String value);

    String getValue();

}
