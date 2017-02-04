package com.buschmais.xo.neo4j.embedded.test.relation.typed.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("A")
public interface A {

    TypedOneToOneRelation getOneToOne();

    List<TypedOneToManyRelation> getOneToMany();

    List<TypedManyToManyRelation> getManyToMany();

}
