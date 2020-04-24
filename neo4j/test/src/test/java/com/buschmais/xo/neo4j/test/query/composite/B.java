package com.buschmais.xo.neo4j.test.query.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("B")
public interface B {

    @Indexed
    String getValue();

    void setValue(String value);

    String getUniqueValue();

    void setUniqueValue(String value);

    A2B getA2B();

}
