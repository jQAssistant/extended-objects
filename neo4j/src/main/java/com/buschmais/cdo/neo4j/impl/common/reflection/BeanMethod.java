package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.reflect.Method;

public class BeanMethod {

    private Method method;

    public BeanMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "BeanMethod{" +
                "method=" + method +
                '}';
    }
}
