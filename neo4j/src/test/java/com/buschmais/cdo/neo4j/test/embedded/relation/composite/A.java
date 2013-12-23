package com.buschmais.cdo.neo4j.test.embedded.relation.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface A {

    @Outgoing
    TypedOneToOneRelation getTypedOneToOne();

    @Outgoing
    List<TypedOneToManyRelation> getTypedOneToMany();

    @Outgoing
    List<TypedManyToManyRelation> getTypedManyToMany();

    @Outgoing
    @QualifiedOneToOneRelation
    B getQualifiedOneToOne();

    void setQualifiedOneToOne(B b);

    @Outgoing
    @QualifiedOneToManyRelation
    List<B> getQualifiedOneToMany();

    @Outgoing
    @QualifiedManyToManyRelation
    List<B> getQualifiedManyToMany();
}
