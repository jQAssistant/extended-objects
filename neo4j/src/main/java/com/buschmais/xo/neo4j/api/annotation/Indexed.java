package com.buschmais.xo.neo4j.api.annotation;

import com.buschmais.xo.spi.annotation.IndexDefinition;

import java.lang.annotation.*;

/**
 * Marks a property as indexed.
 * <p/>
 * <p>An indexed property is used to find instances using {@link com.buschmais.xo.api.XOManager#find(Class, Object)}.</p>
 */
@IndexDefinition
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Indexed {

    /**
     * Indicates that the index shall be created if it does not exist.
     *
     * @return <code>true</code> if the index shall be created.
     */
    boolean create() default true;

    /**
     * Indicates that the index shall enforce a unique constraint.
     *
     * @return <code>true</code> if the index shall enforce a unique constraint.
     */
    boolean unique() default false;

}
