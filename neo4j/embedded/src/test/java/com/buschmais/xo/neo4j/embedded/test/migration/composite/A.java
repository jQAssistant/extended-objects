package com.buschmais.xo.neo4j.embedded.test.migration.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    String getValue();

    void setValue(String value);

}