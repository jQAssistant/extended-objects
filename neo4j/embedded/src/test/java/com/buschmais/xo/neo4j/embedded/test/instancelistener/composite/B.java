package com.buschmais.xo.neo4j.embedded.test.instancelistener.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("B")
public interface B extends Version {

    A2B getA2b();

}
