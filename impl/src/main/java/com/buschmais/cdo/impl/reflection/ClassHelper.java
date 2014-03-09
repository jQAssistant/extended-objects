package com.buschmais.cdo.impl.reflection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.buschmais.cdo.api.CdoException;

public final class ClassHelper {

    private ClassHelper() {
    }

    public static <T> Class<T> getType(String name) {
        Class<T> type;
        try {
            type = (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new CdoException("Cannot find class with name '" + name + "'");
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
            throw new CdoException("Cannot create instance of type '" + type.getName() + "'");
        } catch (IllegalAccessException e) {
            throw new CdoException("Access denied to type '" + type.getName() + "'");
        }
    }
}
