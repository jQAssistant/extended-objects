package com.buschmais.xo.neo4j.embedded.test.relation.typed.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("OneToMany")
public interface TypedOneToManyRelation extends TypedRelation {

    @Outgoing
    A getA();

    @Incoming
    B getB();

}
