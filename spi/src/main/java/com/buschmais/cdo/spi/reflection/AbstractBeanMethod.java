package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AbstractBeanMethod implements BeanMethod {

    private Method method;

    protected AbstractBeanMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return getMethod().getAnnotation(type);
    }
}
