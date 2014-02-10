package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents a set method.
 */
public class SetPropertyMethod extends AbstractPropertyMethod {

    private final GetPropertyMethod getter;

    /**
     * Constructor.
     *
     * @param setter The set method.
     * @param getter The corresponding {@link GetPropertyMethod}.
     * @param name   The name of the property.
     * @param type   The type of the property.
     */
    public SetPropertyMethod(Method setter, GetPropertyMethod getter, String name, Class<?> type, Type genericType) {
        super(setter, name, type, genericType);
        this.getter = getter;
    }

    @Override
    public <T extends Annotation> T getAnnotationOfProperty(Class<T> type) {
        T annotation = getAnnotation(type);
        if (annotation == null) {
            annotation = getter.getAnnotation(type);
        }
        return annotation;
    }
}
