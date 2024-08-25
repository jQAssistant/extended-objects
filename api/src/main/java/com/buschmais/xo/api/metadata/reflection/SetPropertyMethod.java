package com.buschmais.xo.api.metadata.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.buschmais.xo.api.XOException;

/**
 * Represents a set method.
 */
public class SetPropertyMethod extends AbstractPropertyMethod {

    private final GetPropertyMethod getter;

    /**
     * Constructor.
     *
     * @param setter
     *     The set method.
     * @param getter
     *     The corresponding {@link GetPropertyMethod}.
     * @param name
     *     The name of the property.
     * @param type
     *     The type of the property.
     */
    public SetPropertyMethod(Method setter, GetPropertyMethod getter, String name, Class<?> type, Type genericType) {
        super(setter, name, type, genericType);
        if (getter == null) {
            throw new XOException("No getter defined for property '" + name + "' of type '" + type.getName() + "' in type '" + setter.getDeclaringClass()
                .getName() + "'.");
        }
        this.getter = getter;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        if (super.getAnnotation(type) != null) {
            reportInvalidAnnotationLocation();
        }
        return getter.getAnnotation(type);
    }

    @Override
    public <T extends Annotation, M extends Annotation> T getByMetaAnnotation(Class<M> type) {
        if (super.getByMetaAnnotation(type) != null) {
            return reportInvalidAnnotationLocation();
        }
        return getter.getByMetaAnnotation(type);
    }

    @Override
    public Annotation[] getAnnotations() {
        return getter.getAnnotations();
    }

    private <T extends Annotation> T reportInvalidAnnotationLocation() {
        throw new XOException("A setter method must not be annotated, use getter instead: " + getAnnotatedElement().toString());
    }
}
