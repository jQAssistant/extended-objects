package com.buschmais.cdo.neo4j.test.relation.implicit.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;

@Label
public interface B {

    @Incoming
    @ImplicitOneToOne
    A getOneToOne();

    void setOneToOne(A a);
}
