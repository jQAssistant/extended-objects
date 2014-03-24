package com.buschmais.xo.neo4j.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a primitive property of a node.
 * <p>Must be used on get methods of primitives and enumerations and allows overriding the name of the node property.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {

    /**
     * @return The name of the node property.
     */
    String value();
}
