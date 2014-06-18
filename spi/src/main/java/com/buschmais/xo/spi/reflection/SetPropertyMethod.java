package com.buschmais.xo.spi.reflection;

import com.buschmais.xo.api.XOException;

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
        if (getter == null) {
            throw new XOException("No getter defined for property '" + name + "' of type '" + type.getName() + "' in type '" + setter.getDeclaringClass().getName() + "'.");
        }
        this.getter = getter;
    }

    @Override
    public <T extends Annotation> T getAnnotationOfProperty(Class<T> type) {
        return getter.getAnnotation(type);
    }

    @Override
    public <T extends Annotation> T getByMetaAnnotationOfProperty(Class<T> type) {
        return getter.getByMetaAnnotation(type);
    }

    @Override
    public Annotation[] getAnnotationsOfProperty() {
        return getter.getAnnotationsOfProperty();
    }
}
