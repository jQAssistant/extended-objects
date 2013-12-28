package com.buschmais.cdo.neo4j.test.embedded.relation.composite;

import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.ManyToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToOne;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedManyToManyRelation;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedOneToManyRelation;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedOneToOneRelation;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface A {

    // Typed relations without qualifier

    @Outgoing
    TypedOneToOneRelation getTypedOneToOne();

    @Outgoing
    List<TypedOneToManyRelation> getTypedOneToMany();

    @Outgoing
    List<TypedManyToManyRelation> getTypedManyToMany();

    // Typed relations with qualifier

    @Outgoing
    @OneToOne
    TypedRelation getOneToOne();

    @Outgoing
    @OneToMany
    List<TypedRelation> getOneToMany();

    @Outgoing
    @ManyToMany
    List<TypedRelation> getManyToMany();

    // Anonymous relations with qualifier

    @Outgoing
    @OneToOne
    B getDirectOneToOne();

    void setDirectOneToOne(B b);

    @Outgoing
    @OneToMany
    List<B> getDirectOneToMany();

    @Outgoing
    @ManyToMany
    List<B> getDirectManyToMany();
}
