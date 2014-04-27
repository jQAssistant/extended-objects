package com.buschmais.xo.neo4j.test.query.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("A2B")
public interface A2B {

    @Outgoing
    A getA();

    @Incoming
    B getB();

}
