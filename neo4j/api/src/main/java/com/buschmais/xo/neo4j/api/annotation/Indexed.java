package com.buschmais.xo.neo4j.api.annotation;

import java.lang.annotation.*;

import com.buschmais.xo.spi.annotation.IndexDefinition;

/**
 * Marks a property as indexed.
 * <p>
 * An indexed property is used to find instances using
 * {@link com.buschmais.xo.api.XOManager#find(Class, Object)}.
 * </p>
 */
@IndexDefinition
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Indexed {
}
