package com.buschmais.xo.neo4j.test.inheritance.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Value")
public interface ValueDescriptor<V> {

    V getValue();

    void setValue(V value);

}
