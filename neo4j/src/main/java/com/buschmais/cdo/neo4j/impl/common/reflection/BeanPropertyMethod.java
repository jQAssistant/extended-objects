package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.reflect.Method;

public class BeanPropertyMethod extends BeanMethod {

    private MethodType methodType;
    private String name = null;
    private Class<?> type = null;

    public BeanPropertyMethod(Method method, MethodType methodType, String name, Class<?> type) {
        super(method);
        this.methodType = methodType;
        this.name = name;
        this.type = type;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public enum MethodType {
        GETTER,
        SETTER;
    }
}
