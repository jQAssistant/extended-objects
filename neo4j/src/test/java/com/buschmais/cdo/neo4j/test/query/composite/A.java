package com.buschmais.cdo.neo4j.test.query.composite;

import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    @Indexed
    String getValue();

    void setValue(String value);

    A2B getA2B();

}
