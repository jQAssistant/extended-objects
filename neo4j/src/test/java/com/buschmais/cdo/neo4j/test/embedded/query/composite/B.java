package com.buschmais.cdo.neo4j.test.embedded.query.composite;

import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("B")
public interface B {

    @Indexed(unique = true)
    String getValue();

    void setValue(String value);

}
