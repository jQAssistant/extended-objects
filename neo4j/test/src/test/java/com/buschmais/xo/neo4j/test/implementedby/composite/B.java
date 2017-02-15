package com.buschmais.xo.neo4j.test.implementedby.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("B")
public interface B {

    A2B getA2B();

}
