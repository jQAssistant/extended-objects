package com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

@Label
public interface A {

    @Outgoing
    @OneToOne
    B getOneToOne();

    void setOneToOne(B b);

    @Outgoing
    @OneToMany
    List<B> getOneToMany();

    @Outgoing
    @ManyToMany
    List<B> getManyToMany();

}
