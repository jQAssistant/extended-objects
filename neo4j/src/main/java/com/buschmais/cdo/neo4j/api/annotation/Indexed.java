package com.buschmais.cdo.neo4j.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a property as indexed.
 *
 * <p>An indexed property is used to find instances using {@link com.buschmais.cdo.api.CdoManager#find(Class, Object)}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Indexed {
}
