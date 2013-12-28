package com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface A {

    @Outgoing
    @OneToOne
    B getOneToOne();

    @Outgoing
    @OneToMany
    List<B> getOneToMany();

    @Outgoing
    @ManyToMany
    List<B> getManyToMany();

}
