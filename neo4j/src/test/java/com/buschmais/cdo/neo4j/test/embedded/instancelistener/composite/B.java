package com.buschmais.cdo.neo4j.test.embedded.instancelistener.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("B")
public interface B extends Version {

    A2B getA2b();

}
