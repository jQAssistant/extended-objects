package com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;
import com.buschmais.cdo.neo4j.test.embedded.relation.composite.TypedRelation;

@Relation("OneToMany")
public interface TypedOneToManyRelation extends TypedRelation {
}
