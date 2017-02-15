package com.buschmais.xo.neo4j.test.instancelistener.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("A2B")
public interface A2B extends Version {

    @Outgoing
    A getA();

    @Incoming
    B getB();

}
