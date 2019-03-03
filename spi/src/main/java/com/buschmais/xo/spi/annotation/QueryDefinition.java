package com.buschmais.xo.spi.annotation;

import java.lang.annotation.*;

/**
 * Marks an annotation as query definition, i.e. a type represents a query if it
 * is annotated with an annotation which itself is annotated
 * by{@link QueryDefinition}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface QueryDefinition {
}
