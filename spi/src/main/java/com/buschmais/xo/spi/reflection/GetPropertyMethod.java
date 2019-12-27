package com.buschmais.xo.spi.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Represents a get method.
 */
public class GetPropertyMethod extends AbstractPropertyMethod {

    /**
     * Constructor.
     *
     * @param getter
     *            The get method.
     * @param name
     *            The name of the property.
     * @param type
     *            The type of the property.
     */
    public GetPropertyMethod(Method getter, String name, Class<?> type, Type genericType) {
        super(getter, name, type, genericType);
    }

}
