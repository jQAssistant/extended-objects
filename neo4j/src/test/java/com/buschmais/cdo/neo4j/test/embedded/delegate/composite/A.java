package com.buschmais.cdo.neo4j.test.embedded.delegate.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    A2B getA2B();

}
