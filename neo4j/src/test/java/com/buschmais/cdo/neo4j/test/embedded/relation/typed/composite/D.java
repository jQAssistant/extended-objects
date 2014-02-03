package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("D")
public interface D {

    TypeA getTypeA();

    TypeB getTypeB();

}
