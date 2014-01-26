package com.buschmais.cdo.neo4j.test.embedded.issues.not_a_proxy.composite;

import java.util.List;

import com.buschmais.cdo.neo4j.api.annotation.Label;
import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Label("A")
public interface A {

    @Relation("B")
    @Outgoing
    List<B> getB();


}
