package com.buschmais.xo.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Declares a type as final, i.e. it is not possible to extend from it or create
 * composite instances with other types.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Final {
}
