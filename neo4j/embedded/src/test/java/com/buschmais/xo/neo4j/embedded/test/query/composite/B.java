package com.buschmais.xo.neo4j.embedded.test.query.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("B")
public interface B {

    @Indexed(unique = true)
    String getValue();

    void setValue(String value);

    A2B getA2B();

}
