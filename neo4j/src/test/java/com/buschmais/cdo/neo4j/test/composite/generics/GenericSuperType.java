package com.buschmais.cdo.neo4j.test.composite.generics;

public interface GenericSuperType<V> {

    V getValue();

    void setValue(V value);
}
