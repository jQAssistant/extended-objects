package com.buschmais.xo.spi.annotation;


import java.lang.annotation.*;

/**
 * Marks an annotation as index definition, i.e. a property method represents an indexed property if it is annotated with an annotation which itself is annotated by{@link IndexDefinition}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface IndexDefinition {
}
