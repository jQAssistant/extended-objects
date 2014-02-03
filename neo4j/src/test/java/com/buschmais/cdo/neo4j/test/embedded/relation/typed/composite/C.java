package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Label("C")
public interface C {

    TypeA getTypeA();

    TypeB getTypeB();

}
