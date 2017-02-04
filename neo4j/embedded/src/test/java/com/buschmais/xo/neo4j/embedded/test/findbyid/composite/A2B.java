package com.buschmais.xo.neo4j.embedded.test.findbyid.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation
public interface A2B {

    @Outgoing
    A getA();

    @Incoming
    B getB();
}
