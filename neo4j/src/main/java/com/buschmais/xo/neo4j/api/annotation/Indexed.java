package com.buschmais.xo.neo4j.api.annotation;

import com.buschmais.xo.spi.annotation.IndexDefinition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a property as indexed.
 * <p/>
 * <p>An indexed property is used to find instances using {@link com.buschmais.xo.api.XOManager#find(Class, Object)}.</p>
 */
@IndexDefinition
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Indexed {

    boolean create() default false;

    boolean unique() default false;

}
