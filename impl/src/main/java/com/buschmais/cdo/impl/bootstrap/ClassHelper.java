package com.buschmais.cdo.impl.bootstrap;

import com.buschmais.cdo.api.CdoException;

final class ClassHelper {

    private ClassHelper() {
    }

    static <T> Class<T> getType(String name) {
        Class<T> type;
        try {
            type = (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new CdoException("Cannot find class with name " + name);
        }
        return type;
    }

    static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new CdoException("Cannot create instance of type " + type.getName());
        } catch (IllegalAccessException e) {
            throw new CdoException("Access denied to type " + type.getName());
        }
    }
}
