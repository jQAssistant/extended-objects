package com.buschmais.cdo.neo4j.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QueryResult {

    Class<?> query();

    String usingThisAs() default "this";
}
