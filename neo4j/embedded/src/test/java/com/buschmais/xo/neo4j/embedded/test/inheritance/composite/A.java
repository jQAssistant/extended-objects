package com.buschmais.xo.neo4j.embedded.test.inheritance.composite;

import com.buschmais.xo.neo4j.api.annotation.Indexed;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A extends Version {

    @Indexed
    String getIndex();

    void setIndex(String s);

}
