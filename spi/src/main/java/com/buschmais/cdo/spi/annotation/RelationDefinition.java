package com.buschmais.cdo.spi.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an annotation as relation definition, i.e. a type represents a relation if it is annotated with an annotation which itself is annotated by{@link RelationDefinition}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RelationDefinition {
}
