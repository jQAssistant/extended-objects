package com.buschmais.cdo.spi.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks an annotation as relation definition, i.e. a type represents a relation if it is annotated with an annotation which itself is annotated by{@link RelationDefinition}.
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface RelationDefinition {

    @Documented
    @Retention(RUNTIME)
    @Target(ANNOTATION_TYPE)
    public @interface FromDefinition {
    }

    @Documented
    @Retention(RUNTIME)
    @Target(ANNOTATION_TYPE)
    public @interface ToDefinition {
    }
}
