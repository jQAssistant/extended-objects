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

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

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
