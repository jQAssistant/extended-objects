package com.buschmais.cdo.neo4j.test.embedded.migration.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    String getValue();

    void setValue(String value);

}
