package com.buschmais.xo.neo4j.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates if nodes or relationships may be batched.
 *
 * Batching is a performance optimization that collects creating new instances
 * of node or relation types into as few operations on the database as possible.
 *
 * If a type is marked as batchable it must not be referenced by the application
 * over the execution of a flush operation. Such is implicitly triggered on
 * commit or before query execution.
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface Batchable {

    boolean value() default true;

}
