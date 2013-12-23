package com.buschmais.cdo.neo4j.test.embedded.relation.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;

public interface B {

    @Incoming
    TypedOneToOneRelation getTypedOneToOne();

    @Incoming
    TypedOneToManyRelation getTypedOneToMany();

    @Incoming
    List<TypedManyToManyRelation> getTypedManyToMany();

    @Incoming
    @QualifiedOneToOneRelation
    A getQualifiedOneToOne();

    @Incoming
    @QualifiedOneToManyRelation
    List<A> getQualifiedOneToMany();

    @Incoming
    @QualifiedManyToManyRelation
    List<A> getQualifiedManyToMany();

}
