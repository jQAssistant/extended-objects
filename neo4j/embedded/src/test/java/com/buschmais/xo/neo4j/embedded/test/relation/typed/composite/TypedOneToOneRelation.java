package com.buschmais.xo.neo4j.embedded.test.relation.typed.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("OneToOne")
public interface TypedOneToOneRelation extends TypedRelation {

    @Outgoing
    A getA();

    @Incoming
    B getB();

}