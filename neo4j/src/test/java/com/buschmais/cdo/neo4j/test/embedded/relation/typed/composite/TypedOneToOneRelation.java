package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.test.embedded.relation.composite.TypedRelation;

@Relation("OneToOne")
public interface TypedOneToOneRelation extends TypedRelation {
}
