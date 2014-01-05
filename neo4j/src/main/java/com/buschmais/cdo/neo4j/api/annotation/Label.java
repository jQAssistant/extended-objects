package com.buschmais.cdo.neo4j.api.annotation;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.spi.annotation.EntityDefinition;

import java.lang.annotation.*;

/**
 * Defines the label to be used on a node representing a composite object.
 */
@EntityDefinition
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Label {

    /**
     * @return The name of the label.
     */
    String value();

    /**
     * @return The (super) type containing an indexed property ({@link Indexed}).
     *         <p>An index will be created for this label and the indexed property and used by {@link CdoManager#find(Class, Object)}.</p>
     */
    Class<?> usingIndexedPropertyOf() default Object.class;
}
