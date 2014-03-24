package com.buschmais.xo.neo4j.test.relation.implicit.composite;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Relation
@Retention(RUNTIME)
public @interface ImplicitOneToOne {
}
