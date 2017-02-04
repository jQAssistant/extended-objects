package com.buschmais.xo.neo4j.embedded.test.relation.qualified.composite;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("OneToOne")
@Retention(RUNTIME)
public @interface QualifiedOneToOne {
}
