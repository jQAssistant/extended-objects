package com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface B {

    @Outgoing
    @OneToOne
    A getOneToOne();

    @Outgoing
    @OneToMany
    A getManyToOne();

    @Outgoing
    @ManyToMany
    List<A> getManyToMany();

}
