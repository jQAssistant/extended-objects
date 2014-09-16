package com.buschmais.xo.neo4j.test.example.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation
public interface Parent extends Named {

    @Outgoing
    A getParent();

    @Incoming
    A getChild();

}
