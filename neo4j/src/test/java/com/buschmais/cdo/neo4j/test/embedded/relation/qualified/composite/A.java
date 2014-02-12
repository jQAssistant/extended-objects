package com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Label
public interface A {

    @Outgoing
    @QualifiedOneToOne
    B getOneToOne();

    void setOneToOne(B b);

    @Outgoing
    @QualifiedOneToMany
    List<B> getOneToMany();

    @Outgoing
    @QualifiedManyToMany
    List<B> getManyToMany();

}
