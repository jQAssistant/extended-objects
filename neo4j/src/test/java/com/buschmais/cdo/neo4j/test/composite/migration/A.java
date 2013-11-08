package com.buschmais.cdo.neo4j.test.composite.migration;

import com.buschmais.cdo.neo4j.annotation.Indexed;
import com.buschmais.cdo.neo4j.annotation.Label;

@Label("A")
public interface A {

    @Indexed
    String getIndex();

    void setIndex(String index);

}
