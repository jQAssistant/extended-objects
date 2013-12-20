package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Abstract base implementation for property accessor methods (i.e. get/is/set methods).
 */
public abstract class AbstractPropertyMethod extends AbstractAnnotatedElement<Method> implements PropertyMethod {

    private String name = null;
    private Class<?> type = null;

    /**
     * Constructor.
     *
     * @param method The method.
     * @param name   The name of the property.
     * @param type   The type of the property.
     */
    protected AbstractPropertyMethod(Method method, String name, Class<?> type) {
        super(method);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public abstract <T extends Annotation> T getAnnotationOfProperty(Class<T> type);
}
