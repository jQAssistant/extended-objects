package com.buschmais.xo.neo4j.embedded.test.generics.composite;

public interface GenericSuperType<V> {

    V getValue();

    void setValue(V value);
}
