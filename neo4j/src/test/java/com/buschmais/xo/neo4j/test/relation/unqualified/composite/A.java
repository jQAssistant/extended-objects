package com.buschmais.xo.neo4j.test.relation.unqualified.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

public interface A {

    @Outgoing
    @Relation("OneToOne")
    B getOneToOne();

    void setOneToOne(B b);

    @Outgoing
    @Relation("OneToMany")
    List<B> getOneToMany();

    @Outgoing
    @Relation("ManyToMany")
    List<B> getManyToMany();
}
