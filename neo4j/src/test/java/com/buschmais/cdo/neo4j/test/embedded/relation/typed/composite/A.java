package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Label("A")
public interface A {

    TypedOneToOneRelation getOneToOne();

    @Outgoing
    List<TypedOneToManyRelation> getOneToMany();

    @Outgoing
    List<TypedManyToManyRelation> getManyToMany();

}
