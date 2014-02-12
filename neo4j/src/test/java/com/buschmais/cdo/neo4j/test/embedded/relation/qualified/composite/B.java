package com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;

@Label
public interface B {

    @Incoming
    @OneToOne
    A getOneToOne();

    void setOneToOne(A a);

    @Incoming
    @OneToMany
    A getManyToOne();

    @Incoming
    @ManyToMany
    List<A> getManyToMany();

}
