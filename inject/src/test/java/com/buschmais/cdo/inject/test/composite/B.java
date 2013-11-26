package com.buschmais.cdo.inject.test.composite;

import com.buschmais.cdo.neo4j.api.annotation.Indexed;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("B")
public interface B {

    @Indexed
    public String getValue();

    public void setValue(String value);

}
