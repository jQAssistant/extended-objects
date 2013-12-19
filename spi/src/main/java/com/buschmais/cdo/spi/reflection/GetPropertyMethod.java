package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Represents a get method.
 */
public class GetPropertyMethod extends AbstractPropertyMethod {

    /**
     * Constructor.
     *
     * @param getter The get method.
     * @param name   The name of the property.
     * @param type   The type of the property.
     */
    public GetPropertyMethod(Method getter, String name, Class<?> type) {
        super(getter, name, type);
    }

    @Override
    public <T extends Annotation> T getAnnotationOfProperty(Class<T> type) {
        return getAnnotation(type);
    }

}
