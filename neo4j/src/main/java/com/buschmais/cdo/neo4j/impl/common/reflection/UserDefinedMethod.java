package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class UserDefinedMethod implements BeanMethod {

    private Method method;

    public UserDefinedMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return method.getAnnotation(type);
    }

    @Override
    public String toString() {
        return "UserDefinedMethod{" +
                "method=" + method +
                '}';
    }
}
