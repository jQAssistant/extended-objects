package com.buschmais.xo.neo4j.test.relation.implicit.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface B {

    @Incoming
    @ImplicitOneToOne
    A getOneToOne();

    void setOneToOne(A a);
}
