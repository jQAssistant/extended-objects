package com.buschmais.xo.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares a type as abstract, i.e. it is only allowed to createa an instance
 * from a non-abstract extending type or as a composite with at least
 * non-abstract type.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Abstract {
}
