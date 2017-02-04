package com.buschmais.xo.neo4j.embedded.test.relation.qualified.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label
public interface B {

    @Incoming
    @QualifiedOneToOne
    A getOneToOne();

    void setOneToOne(A a);

    @Incoming
    @QualifiedOneToMany
    A getManyToOne();

    @Incoming
    @QualifiedManyToMany
    List<A> getManyToMany();

}
