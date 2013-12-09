package com.buschmais.cdo.neo4j.test.embedded.inheritance.composite;

import com.buschmais.cdo.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A extends Version{

    @Indexed
    String getIndex();

    void setIndex(String s);

}
