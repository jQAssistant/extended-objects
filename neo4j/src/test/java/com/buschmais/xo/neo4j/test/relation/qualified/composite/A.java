package com.buschmais.xo.neo4j.test.relation.qualified.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

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
