package com.buschmais.cdo.neo4j.test.relation.typed.composite;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface BaseType {

    @Outgoing
    C getC();

    @Incoming
    D getD();

    int getVersion();

    void setVersion(int version);

}
