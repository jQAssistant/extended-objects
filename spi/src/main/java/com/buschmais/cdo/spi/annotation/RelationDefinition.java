package com.buschmais.cdo.spi.annotation;


import java.lang.annotation.*;

/**
 * Marks an annotation as relation definition, i.e. a type represents a relation if it is annotated with an annotation which itself is annotated by{@link RelationDefinition}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RelationDefinition {
}
