package com.buschmais.xo.neo4j.test.relation.unqualified.composite;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

public interface B {

    @Incoming
    @Relation("OneToOne")
    A getOneToOne();

    void setOneToOne(A a);

    @Incoming
    @Relation("OneToMany")
    A getManyToOne();

    @Incoming
    @Relation("ManyToMany")
    List<A> getManyToMany();

}
