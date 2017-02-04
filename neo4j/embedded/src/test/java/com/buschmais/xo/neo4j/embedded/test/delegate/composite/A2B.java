package com.buschmais.xo.neo4j.embedded.test.delegate.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("RELATION")
public interface A2B {

    @Outgoing
    A getA();

    @Incoming
    B getB();
}
