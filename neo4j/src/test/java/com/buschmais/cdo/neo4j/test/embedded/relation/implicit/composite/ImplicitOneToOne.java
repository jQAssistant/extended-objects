package com.buschmais.cdo.neo4j.test.embedded.relation.implicit.composite;

import com.buschmais.cdo.neo4j.api.annotation.Relation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Relation
@Retention(RUNTIME)
public @interface ImplicitOneToOne {
}
