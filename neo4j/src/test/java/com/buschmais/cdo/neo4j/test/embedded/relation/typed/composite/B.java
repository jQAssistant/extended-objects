package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;

@Label("B")
public interface B {

    TypedOneToOneRelation getOneToOne();

    @Incoming
    TypedOneToManyRelation getManyToOne();

    @Incoming
    List<TypedManyToManyRelation> getManyToMany();

}
