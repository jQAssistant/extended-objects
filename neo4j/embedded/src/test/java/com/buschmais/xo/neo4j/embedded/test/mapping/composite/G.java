package com.buschmais.xo.neo4j.embedded.test.mapping.composite;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("G")
public interface G {

    @Outgoing
    @Relation("ONE_TO_ONE")
    H getOneToOneH();

    void setOneToOneH(H h);

    @Outgoing
    @Relation("ONE_TO_MANY")
    List<H> getOneToManyH();

    @Outgoing
    @Relation("MANY_TO_MANY")
    List<H> getManyToManyH();

}
