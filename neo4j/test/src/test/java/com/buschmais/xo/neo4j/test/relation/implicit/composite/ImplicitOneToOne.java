package com.buschmais.xo.neo4j.test.relation.implicit.composite;

import java.lang.annotation.Retention;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Relation
@Retention(RUNTIME)
public @interface ImplicitOneToOne {
}
