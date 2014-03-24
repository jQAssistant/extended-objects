package com.buschmais.xo.neo4j.test.relation.typed.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("D")
public interface D {

    TypeA getTypeA();

    TypeB getTypeB();

}
