package com.buschmais.cdo.spi.reflection;

/**
 * Represents an annotated class.
 */
public class AnnotatedType extends AbstractAnnotatedElement<Class<?>> {

    /**
     * The annotated class.
     */
    public AnnotatedType(Class<?> annotated) {
        super(annotated);
    }

}
