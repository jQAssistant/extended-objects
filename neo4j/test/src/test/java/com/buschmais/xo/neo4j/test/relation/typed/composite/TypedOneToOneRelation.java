package com.buschmais.xo.neo4j.test.relation.typed.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.From;
import com.buschmais.xo.neo4j.api.annotation.Relation.To;

@Relation("OneToOne")
public interface TypedOneToOneRelation extends TypedRelation {

    @From
    A getA();

    @To
    B getB();

}
