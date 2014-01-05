package com.buschmais.cdo.spi.annotation;


import java.lang.annotation.*;

/**
 * Marks an annotation as entity definition, i.e. a type represents an entity if it is annotated with an annotation which itself is annotated by{@link EntityDefinition}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface EntityDefinition {
}
