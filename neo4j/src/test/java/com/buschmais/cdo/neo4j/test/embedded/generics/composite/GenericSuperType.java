package com.buschmais.cdo.neo4j.test.embedded.generics.composite;

public interface GenericSuperType<V> {

    V getValue();

    void setValue(V value);
}
