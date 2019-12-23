package com.buschmais.xo.neo4j.test.relation.filtered.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label
public interface A {

    @Relation("ONE_TO_ONE")
    B getB();

    void setB(B b);

    @Relation("ONE_TO_ONE")
    C getC();

    void setC(C c);

    @Relation("ONE_TO_MANY")
    List<B> getListOfB();

    @Relation("ONE_TO_MANY")
    List<C> getListOfC();

}
