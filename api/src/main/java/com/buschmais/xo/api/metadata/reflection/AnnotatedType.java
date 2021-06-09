package com.buschmais.xo.api.metadata.reflection;

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

    @Override
    public String getName() {
        return getAnnotatedElement().getSimpleName();
    }
}
