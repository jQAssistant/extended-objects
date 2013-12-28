package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import com.buschmais.cdo.neo4j.test.embedded.relation.composite.*;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.ManyToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToOne;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface B {

    @Incoming
    TypedOneToOneRelation getOneToOne();

    @Incoming
    TypedOneToManyRelation getManyToOne();

    @Incoming
    List<TypedManyToManyRelation> getManyToMany();

}
