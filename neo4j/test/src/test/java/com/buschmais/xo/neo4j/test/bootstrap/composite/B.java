package com.buschmais.xo.neo4j.test.bootstrap.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label("B")
public interface B {

    @Outgoing
    B2B getOutgoingB2B();

    @Incoming
    B2B getIncomingB2B();
}
