package com.buschmais.xo.neo4j.test.batchable.composite;

import com.buschmais.xo.neo4j.api.annotation.Batchable;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation
@Batchable
public interface A2B {

    @Outgoing
    A getA();

    @Incoming
    B getB();
}
