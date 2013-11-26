package com.buschmais.cdo.inject.test.composite;

import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    @Indexed
    public String getValue();

    public void setValue(String value);
}
