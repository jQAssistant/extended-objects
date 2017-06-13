package com.buschmais.xo.neo4j.test.generics.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface Value<T> {

    T getValue();

    void setValue(T value);

}
