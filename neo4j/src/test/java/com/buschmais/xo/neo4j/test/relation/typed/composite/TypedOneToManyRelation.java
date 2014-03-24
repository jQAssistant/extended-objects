package com.buschmais.xo.neo4j.test.relation.typed.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("OneToMany")
public interface TypedOneToManyRelation extends TypedRelation {

    @Outgoing
    A getA();

    @Incoming
    B getB();

}
