package com.buschmais.xo.neo4j.test.example.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface A extends Named {

    @Indexed
    String getValue();

    void setValue(String value);

}
