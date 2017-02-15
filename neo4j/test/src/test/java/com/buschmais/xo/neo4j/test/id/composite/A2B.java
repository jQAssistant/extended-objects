package com.buschmais.xo.neo4j.test.id.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation
public interface A2B {
    @Relation.Outgoing
    A getA();

    @Relation.Incoming
    B getB();
}
