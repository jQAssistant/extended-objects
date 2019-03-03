package com.buschmais.xo.neo4j.api.annotation;

import java.lang.annotation.*;

/**
 * Defines a primitive property of a node.
 * <p>
 * Must be used on get methods of primitives and enumerations and allows
 * overriding the name of the node property.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Property {

    /**
     * @return The name of the node property.
     */
    String value();
}
