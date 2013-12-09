package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class PropertyMethod implements BeanMethod {

    private Method getter;
    private Method setter;
    private String name = null;
    private Class<?> type = null;

    public PropertyMethod(Method getter, Method setter, String name, Class<?> type) {
        this.getter = getter;
        this.setter = setter;
        this.name = name;
        this.type = type;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public Method getMethod() {
        return getter != null ? getter : setter;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        T annotation = null;
        if (getter != null) {
            annotation = getter.getAnnotation(type);
        }
        if (annotation == null && setter != null) {
            annotation = setter.getAnnotation(type);
        }
        return annotation;
    }
}
