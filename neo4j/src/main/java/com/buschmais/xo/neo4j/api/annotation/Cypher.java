package com.buschmais.xo.neo4j.api.annotation;

import com.buschmais.xo.spi.annotation.QueryDefinition;

import java.lang.annotation.*;

/**
 * Marks an interface or method as a CYPHER query.
 */
@QueryDefinition
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cypher {

    /**
     * @return The CYPHER expression.
     */
    String value();

}
