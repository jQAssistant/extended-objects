package com.buschmais.cdo.neo4j.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an (outgoing) relationship of a node.
 * <p>Must be used on get methods references or collections of other composite objects and allows overriding the name of the relationship.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Relation {
    String value();
}
