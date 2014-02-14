package com.buschmais.cdo.neo4j.test.delegate.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Relation("RELATION")
public interface A2B {

    @Outgoing
    A getA();

    @Incoming
    B getB();
}
