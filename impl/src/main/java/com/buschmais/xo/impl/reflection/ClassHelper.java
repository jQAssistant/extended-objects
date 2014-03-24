package com.buschmais.xo.impl.reflection;

import com.buschmais.xo.api.XOException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ClassHelper {

    private ClassHelper() {
    }

    public static <T> Class<T> getType(String name) {
        Class<T> type;
        try {
            type = (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new XOException("Cannot find class with name '" + name + "'");
        }
        return type;
    }

    public static Collection<Class<?>> getTypes(Collection<String> typeNames) {
        Set<Class<?>> types = new HashSet<>();
        for (String typeName : typeNames) {
            types.add(ClassHelper.getType(typeName));
        }
        return types;
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new XOException("Cannot create instance of type '" + type.getName() + "'");
        } catch (IllegalAccessException e) {
            throw new XOException("Access denied to type '" + type.getName() + "'");
        }
    }
}
