package com.buschmais.cdo.neo4j.api.annotation;

import com.buschmais.cdo.spi.annotation.RelationDefinition;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines an relationship of a node.
 * <p>Must be used on get methods references or collections of other composite objects and allows overriding the name of the relationship.</p>
 */
@RelationDefinition
@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE, METHOD})
public @interface Relation {

    /**
     * @return The name of the relation.
     */
    String value();

    @Retention(RUNTIME)
    @Target({METHOD})
    public @interface Incoming {
    }

    @Retention(RUNTIME)
    @Target({METHOD})
    public @interface Outgoing {
    }
}
