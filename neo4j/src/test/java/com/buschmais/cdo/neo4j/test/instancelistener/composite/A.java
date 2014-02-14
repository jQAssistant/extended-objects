package com.buschmais.cdo.neo4j.test.instancelistener.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("A")
public interface A extends Version {

    A2B getA2b();
}
