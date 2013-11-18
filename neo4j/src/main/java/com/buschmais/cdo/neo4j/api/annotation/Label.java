package com.buschmais.cdo.neo4j.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the label to be used on a node representing an composite object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Label {

    /**
     * @return The name of the label.
     */
    String value();

    /**
     * @return The (super) type containing an indexed property ({@link Indexed}).
     *         <p>An index will be created for this label and the indexed property and used by {@link com.buschmais.cdo.api.CdoManager#find(Class, Object)}.</p>
     */
    Class<?> usingIndexedPropertyOf() default Object.class;
}
