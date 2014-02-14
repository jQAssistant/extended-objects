package com.buschmais.cdo.neo4j.test.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

@Label("B")
public interface B {

    TypedOneToOneRelation getOneToOne();

    TypedOneToManyRelation getManyToOne();

    List<TypedManyToManyRelation> getManyToMany();

}
