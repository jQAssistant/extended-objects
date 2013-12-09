package com.buschmais.cdo.neo4j.impl.common.reflection;

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
