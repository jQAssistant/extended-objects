package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Defines the interface for property accessor methods (i.e. get/is/set methods).
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

    /**
     * Return an annotation which is present on the property (i.e. including the get/is method if it is not present on the set method).
     *
     * @param type The annotation type.
     * @param <T>  The annotation type.
     * @return The annotation or <code>null</code>.
     */
    <T extends Annotation> T getAnnotationOfProperty(Class<T> type);
}
