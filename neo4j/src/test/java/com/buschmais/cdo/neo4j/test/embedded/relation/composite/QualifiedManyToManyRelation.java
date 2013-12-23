package com.buschmais.cdo.neo4j.test.embedded.relation.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Relation("QualifiedManyToManyRelation")
@Retention(RetentionPolicy.RUNTIME)
public @interface QualifiedManyToManyRelation {
}
