package com.buschmais.xo.neo4j.embedded.test.relation.implicit.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface A {

    @Outgoing
    @ImplicitOneToOne
    B getOneToOne();

    void setOneToOne(B b);

}
