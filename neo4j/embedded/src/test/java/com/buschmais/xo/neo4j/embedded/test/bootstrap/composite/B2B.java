package com.buschmais.xo.neo4j.embedded.test.bootstrap.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("B2B")
public interface B2B {

    @Outgoing
    B getChild();

    @Incoming
    B getParent();

}
