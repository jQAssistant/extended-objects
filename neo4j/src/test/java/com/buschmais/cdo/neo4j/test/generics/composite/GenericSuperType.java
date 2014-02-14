package com.buschmais.cdo.neo4j.test.generics.composite;

public interface GenericSuperType<V> {

    V getValue();

    void setValue(V value);
}
