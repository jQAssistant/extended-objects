package com.buschmais.xo.neo4j.doc.relation.qualified;

import java.lang.annotation.Retention;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

// tag::Class[]
@Relation
@Retention(RUNTIME)
public @interface ActedIn {
}
// end::Class[]
