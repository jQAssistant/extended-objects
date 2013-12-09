package com.buschmais.cdo.neo4j.test.embedded.query.composite;

import com.buschmais.cdo.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    @Indexed
    String getValue();

    void setValue(String value);

}
