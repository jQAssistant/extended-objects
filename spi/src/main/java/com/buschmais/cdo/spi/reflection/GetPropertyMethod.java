package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
    public GetPropertyMethod(Method getter, String name, Class<?> type, Type genericType) {
        super(getter, name, type, genericType);
    }

    @Override
    public <T extends Annotation> T getAnnotationOfProperty(Class<T> type) {
        return getAnnotation(type);
    }

    @Override
    public <T extends Annotation> T getByMetaAnnotationOfProperty(Class<T> type) {
        return getByMetaAnnotation(type);
    }

    @Override
    public Annotation[] getAnnotationsOfProperty() {
        return getAnnotations();
    }

}
