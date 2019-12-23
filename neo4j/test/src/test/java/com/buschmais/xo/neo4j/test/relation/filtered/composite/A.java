package com.buschmais.xo.neo4j.test.relation.filtered.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label
public interface A {

    @Relation("ONE_TO_MANY")
    List<B> getB();

    @Relation("ONE_TO_MANY")
    List<C> getC();

}
