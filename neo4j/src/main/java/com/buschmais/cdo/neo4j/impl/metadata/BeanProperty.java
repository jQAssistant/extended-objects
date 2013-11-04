package com.buschmais.cdo.neo4j.impl.metadata;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
* Created with IntelliJ IDEA.
* User: dimahler
* Date: 11/4/13
* Time: 6:04 PM
* To change this template use File | Settings | File Templates.
*/
public class BeanProperty {
    private String name = null;
    private Class<?> type = null;
    private Type genericType = null;
    private Method getter = null;
    private Method setter = null;

    public BeanProperty(String name, Class<?> type, Type genericType) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }
}
