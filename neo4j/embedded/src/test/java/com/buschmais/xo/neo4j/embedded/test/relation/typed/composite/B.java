package com.buschmais.xo.neo4j.embedded.test.relation.typed.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("B")
public interface B {

    TypedOneToOneRelation getOneToOne();

    TypedOneToManyRelation getManyToOne();

    List<TypedManyToManyRelation> getManyToMany();

}
