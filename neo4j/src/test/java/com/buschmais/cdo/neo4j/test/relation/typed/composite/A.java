package com.buschmais.cdo.neo4j.test.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

@Label("A")
public interface A {

    TypedOneToOneRelation getOneToOne();

    List<TypedOneToManyRelation> getOneToMany();

    List<TypedManyToManyRelation> getManyToMany();

}
