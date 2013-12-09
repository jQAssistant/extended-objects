package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AbstractPropertyMethod extends AbstractBeanMethod implements PropertyMethod {

    private String name = null;
    private Class<?> type = null;

    protected AbstractPropertyMethod(Method method, String name, Class<?> type) {
        super(method);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public abstract <T extends Annotation> T getPropertyAnnotation(Class<T> type);
}
