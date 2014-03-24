package com.buschmais.xo.neo4j.test.issues.not_a_proxy.composite;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;

@Label("A")
public interface A {

    @Relation("B")
    @Outgoing
    List<B> getB();


}
