package com.buschmais.xo.neo4j.embedded.test.instancelistener.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A extends Version {

    A2B getA2b();
}
