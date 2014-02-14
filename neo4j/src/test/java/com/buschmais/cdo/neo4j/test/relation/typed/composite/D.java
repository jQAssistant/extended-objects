package com.buschmais.cdo.neo4j.test.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("D")
public interface D {

    TypeA getTypeA();

    TypeB getTypeB();

}
