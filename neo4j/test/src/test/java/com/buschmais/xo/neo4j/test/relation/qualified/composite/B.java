package com.buschmais.xo.neo4j.test.relation.qualified.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

@Label
public interface B {

    @Incoming
    @QualifiedOneToOne
    A getOneToOne();

    void setOneToOne(A a);

    @Incoming
    @QualifiedOneToMany
    A getManyToOne();

    void setManyToOne(A a);

    @Incoming
    @QualifiedManyToMany
    List<A> getManyToMany();

}
