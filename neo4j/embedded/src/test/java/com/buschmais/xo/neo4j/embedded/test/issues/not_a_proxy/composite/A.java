package com.buschmais.xo.neo4j.embedded.test.issues.not_a_proxy.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label("A")
public interface A {

    @Relation("B")
    @Outgoing
    List<B> getB();


}
