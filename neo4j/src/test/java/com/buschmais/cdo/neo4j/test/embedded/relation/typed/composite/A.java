package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface A {

    @Outgoing
    TypedOneToOneRelation getOneToOne();

    @Outgoing
    List<TypedOneToManyRelation> getOneToMany();

    @Outgoing
    List<TypedManyToManyRelation> getManyToMany();

}
