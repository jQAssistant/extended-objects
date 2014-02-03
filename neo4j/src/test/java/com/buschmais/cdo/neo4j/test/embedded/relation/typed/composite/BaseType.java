package com.buschmais.cdo.neo4j.test.embedded.relation.composite;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.C;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.D;

public interface BaseType {

    @Outgoing
    C getC();

    @Incoming
    D getD();

    int getVersion();

    void setVersion(int version);

}
