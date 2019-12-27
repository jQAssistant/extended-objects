package com.buschmais.xo.spi.reflection;

import java.lang.reflect.Type;

/**
 * Defines the interface for property accessor methods (i.e. get/is/set
 * methods).
 */
public interface PropertyMethod extends AnnotatedMethod {

    /**
     * Return the name of the property.
     *
     * @return The name of the property.
     */
    String getName();

    /**
     * Return the type of the property.
     *
     * @return The type of the property.
     */
    Class<?> getType();

    /**
     * Return the generic type of the property.
     *
     * @return The generic type of the property.
     */
    Type getGenericType();
}
