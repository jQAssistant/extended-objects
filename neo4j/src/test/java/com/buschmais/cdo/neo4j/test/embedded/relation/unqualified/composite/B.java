package com.buschmais.cdo.neo4j.test.embedded.relation.unqualified.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.ManyToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToOne;
import com.buschmais.cdo.neo4j.test.embedded.relation.composite.TypedRelation;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedManyToManyRelation;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedOneToManyRelation;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedOneToOneRelation;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

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
