package com.buschmais.cdo.neo4j.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ResultOf {

    Class<?> query() default Object.class;

    String usingThisAs() default "this";

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Parameter {
        String value();
    }

}
