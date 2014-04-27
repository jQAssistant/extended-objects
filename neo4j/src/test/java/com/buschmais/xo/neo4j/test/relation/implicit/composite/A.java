package com.buschmais.xo.neo4j.test.relation.implicit.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label
public interface A {

    @Outgoing
    @ImplicitOneToOne
    B getOneToOne();

    void setOneToOne(B b);

}
