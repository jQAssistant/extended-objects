package com.buschmais.xo.neo4j.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.buschmais.xo.spi.annotation.QueryDefinition;

/**
 * Marks an interface or method as a Lucene query.
 */
@QueryDefinition
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Lucene {

    /**
     * @return The Lucene expression.
     */
    String value();

    Class<?> type();
}
