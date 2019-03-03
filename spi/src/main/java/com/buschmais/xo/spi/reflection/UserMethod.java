package com.buschmais.xo.spi.reflection;

import java.lang.reflect.Method;

/**
 * Represents a method which cannot be mapped to a property.
 */
public class UserMethod extends AbstractAnnotatedElement<Method> implements AnnotatedMethod {

    /**
     * Constructor.
     *
     * @param method
     *            The method.
     */
    public UserMethod(Method method) {
        super(method);
    }

    @Override
    public String getName() {
        return getAnnotatedElement().getName();
    }
}
