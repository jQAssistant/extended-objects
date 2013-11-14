package com.buschmais.cdo.neo4j.test.invokeusing.composite;

import com.buschmais.cdo.neo4j.api.annotation.InvokeUsing;
import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    int getValue();

    void setValue(int i);

    @InvokeUsing(SetMethod.class)
    void setUsingHandler(String usingHandler);

    @InvokeUsing(GetMethod.class)
    String getUsingHandler();

    @InvokeUsing(IncrementValueMethod.class)
    int incrementValue();

}
