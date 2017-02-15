package com.buschmais.xo.neo4j.test.relation.qualified.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;

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
