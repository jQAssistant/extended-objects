package com.buschmais.xo.neo4j.test.relation.qualified.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Relation("OneToMany")
@Retention(RetentionPolicy.RUNTIME)
public @interface QualifiedOneToMany {
}
