package com.buschmais.xo.neo4j.test.bootstrap.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("B2B")
public interface B2B {

    @Outgoing
    B getChild();

    @Incoming
    B getParent();

}
