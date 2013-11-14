package com.buschmais.cdo.neo4j.test.invokeusing.composite;

import com.buschmais.cdo.neo4j.api.annotation.InvokeUsing;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    int getValue();

    void setValue(int i);

    @InvokeUsing(IncrementValueMethod.class)
    int incrementValue();

}
