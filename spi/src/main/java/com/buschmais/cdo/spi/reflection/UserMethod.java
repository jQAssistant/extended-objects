package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class UserMethod implements BeanMethod {

    private Method method;

    public UserMethod(Method method) {
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
        return "UserMethod{" +
                "method=" + method +
                '}';
    }
}
