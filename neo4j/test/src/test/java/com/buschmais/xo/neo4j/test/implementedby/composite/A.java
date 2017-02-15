package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.api.annotation.ImplementedBy;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A extends Comparable<A> {

    int getValue();

    void setValue(int i);

    @ImplementedBy(SetMethod.class)
    void setCustomValue(String usingHandler);

    @ImplementedBy(GetMethod.class)
    String getCustomValue();

    @ImplementedBy(EntityIncrementValueMethod.class)
    int incrementValue();

    @Override
    @ImplementedBy(CompareToMethod.class)
    int compareTo(A other);

    void unsupportedOperation();

    A2B getA2B();

}
