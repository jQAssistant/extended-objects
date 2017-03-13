package com.buschmais.xo.neo4j.doc.dynamic;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.buschmais.xo.neo4j.api.annotation.Relation;

// tag::Class[]
@Relation
@Retention(RUNTIME)
public @interface ActedIn {
}
// end::Class[]
