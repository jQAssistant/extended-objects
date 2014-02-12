package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Relation("OneToOne")
public interface TypedOneToOneRelation extends TypedRelation {

    @Outgoing
    A getA();

    @Incoming
    B getB();

}
