package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class SetPropertyMethod extends AbstractPropertyMethod {

    private GetPropertyMethod getter;

    public SetPropertyMethod(Method setter, GetPropertyMethod getter, String name, Class<?> type) {
        super(setter, name, type);
        this.getter = getter;
    }

    @Override
    public <T extends Annotation> T getPropertyAnnotation(Class<T> type) {
        T annotation = getAnnotation(type);
        if (annotation == null) {
            annotation = getter.getAnnotation(type);
        }
        return annotation;
    }
}
