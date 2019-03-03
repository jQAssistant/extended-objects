package com.buschmais.xo.spi.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.buschmais.xo.api.XOException;

/**
 * Provides helper methods for working with Java classes.
 */
public final class ClassHelper {

    /**
     * Private constructor.
     */
    private ClassHelper() {
    }

    /**
     * Load a class.
     * 
     * @param name
     *            The class name.
     * @param <T>
     *            The expected class type.
     * @return The class.
     */
    public static <T> Class<T> getType(String name) {
        Class<T> type;
        try {
            type = (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new XOException("Cannot find class with name '" + name + "'", e);
        }
        return type;
    }

    /**
     * Load a collection of classes.
     * 
     * @param typeNames
     *            The class names.
     * @return The collection of classes.
     */
    public static Collection<Class<?>> getTypes(Collection<String> typeNames) {
        Set<Class<?>> types = new HashSet<>();
        for (String typeName : typeNames) {
            types.add(ClassHelper.getType(typeName));
        }
        return types;
    }

    /**
     * Create an instance of a class.
     * 
     * @param type
     *            The class.
     * @param <T>
     *            The class type.
     * @return The instance.
     */
    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new XOException("Cannot create instance of type '" + type.getName() + "'", e);
        } catch (IllegalAccessException e) {
            throw new XOException("Access denied to type '" + type.getName() + "'", e);
        }
    }

    /**
     * Determines the type of a class implementing a generic interface with one type
     * parameter.
     * 
     * @param genericType
     *            The generic interface type.
     * @param type
     *            The type to determine the parameter from.
     * @return The type parameter.
     */
    public static Class<?> getTypeParameter(Class<?> genericType, Class<?> type) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                if (genericType.equals(parameterizedType.getRawType())) {
                    Type typeParameter = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                    if (typeParameter instanceof Class<?>) {
                        return (Class<?>) typeParameter;
                    }
                }
            }
        }
        return null;
    }
}
