package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;

/**
 * Abstract base implementation for annotated elements.
 *
 * @param <AE> The annotated element type.
 */
public abstract class AbstractAnnotatedElement<AE extends java.lang.reflect.AnnotatedElement> implements AnnotatedElement<AE> {

    private final AE annotated;

    /**
     * Constructor.
     *
     * @param annotated The annotated element.
     */
    protected AbstractAnnotatedElement(AE annotated) {
        this.annotated = annotated;
    }

    @Override
    public AE getAnnotatedElement() {
        return annotated;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return annotated.getAnnotation(type);
    }

    @Override
    public <T extends Annotation, M extends Annotation> T getByMetaAnnotation(Class<M> type) {
        for (Annotation annotation : annotated.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(type)) {
                return (T) annotation;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAnnotatedElement that = (AbstractAnnotatedElement) o;
        if (!annotated.equals(that.annotated)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return annotated.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "annotated=" + annotated +
                '}';
    }
}
