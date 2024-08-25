package com.buschmais.xo.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Controls the auto flush behavior of a query, i.e if {@link #value()} is
 * <code>true</code> then pending changes will be flushed to the datastore
 * before the query is executed.
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Flush {

    boolean value();

}
