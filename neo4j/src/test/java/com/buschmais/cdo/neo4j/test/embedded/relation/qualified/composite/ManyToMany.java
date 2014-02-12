package com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Relation("QualifiedManyToMany")
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToMany {
}
