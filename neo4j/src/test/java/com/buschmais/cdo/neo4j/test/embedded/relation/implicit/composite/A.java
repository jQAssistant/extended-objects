package com.buschmais.cdo.neo4j.test.embedded.relation.implicit.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Label
public interface A {

    @Outgoing
    @ImplicitOneToOne
    B getOneToOne();

    void setOneToOne(B b);

}
