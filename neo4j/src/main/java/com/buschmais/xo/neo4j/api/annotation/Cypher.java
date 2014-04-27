package com.buschmais.xo.neo4j.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.buschmais.xo.spi.annotation.QueryDefinition;

/**
 *
 * TODO: same as in xo-tinkerpop
 *
 * Marks an interface or method as a CYPHER query.
 */
@QueryDefinition
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cypher {

    /**
     * @return The CYPHER expression.
     */
    String value();

}
