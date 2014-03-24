package com.buschmais.xo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Abstract base implementation for property accessor methods (i.e. get/is/set methods).
 */
public abstract class AbstractPropertyMethod extends AbstractAnnotatedElement<Method> implements PropertyMethod {

    private final String name;
    private final Class<?> type;
    private final Type genericType;

    /**
     * Constructor.
     *
     * @param method The method.
     * @param name   The name of the property.
     * @param type   The type of the property.
     */
    protected AbstractPropertyMethod(Method method, String name, Class<?> type, Type genericType) {
        super(method);
        this.name = name;
        this.type = type;
        this.genericType = genericType;
    }

    @Override
    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }

    public abstract <T extends Annotation> T getAnnotationOfProperty(Class<T> type);

    public abstract <T extends Annotation> T getByMetaAnnotationOfProperty(Class<T> type);
}
