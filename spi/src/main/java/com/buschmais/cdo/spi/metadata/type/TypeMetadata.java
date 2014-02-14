package com.buschmais.cdo.spi.metadata.type;

import com.buschmais.cdo.spi.metadata.method.MethodMetadata;
import com.buschmais.cdo.spi.reflection.AnnotatedType;

import java.util.Collection;

/**
 * Defines the base interface describing metadata of a type. (i.e. entity or relation).
 */
public interface TypeMetadata {

    /**
     * Return the annotated type representing the type.
     *
     * @return The annotated type.
     */
    AnnotatedType getAnnotatedType();

    /**
     * Return the metadata of all super types.
     *
     * @return The metadata of all super types.
     */
    Collection<TypeMetadata> getSuperTypes();

    /**
     * Return the metadata of all properties declared in the type.
     *
     * @return The metadata of all properties declared in the type
     */
    Collection<MethodMetadata<?, ?>> getProperties();

}
