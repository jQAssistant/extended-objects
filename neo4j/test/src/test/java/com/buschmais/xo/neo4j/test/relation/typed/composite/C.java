package com.buschmais.xo.neo4j.test.relation.typed.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("C")
public interface C {

    TypeA getTypeA();

    TypeB getTypeB();

}
