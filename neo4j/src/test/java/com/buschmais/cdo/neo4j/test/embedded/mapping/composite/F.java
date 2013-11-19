package com.buschmais.cdo.neo4j.test.embedded.mapping.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("F")
public interface F {

    void setValue(String value);

    String getValue();

}
