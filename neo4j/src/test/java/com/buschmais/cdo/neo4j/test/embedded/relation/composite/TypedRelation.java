package com.buschmais.cdo.neo4j.test.embedded.relation.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

public interface TypedRelation {

    int getVersion();
    void setVersion(int version);

}
