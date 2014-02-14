package com.buschmais.cdo.neo4j.test.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("C")
public interface C {

    TypeA getTypeA();

    TypeB getTypeB();

}
