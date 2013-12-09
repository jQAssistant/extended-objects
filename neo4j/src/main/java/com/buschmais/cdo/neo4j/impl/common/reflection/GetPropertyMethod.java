package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class GetPropertyMethod extends AbstractPropertyMethod {


    public GetPropertyMethod(Method getter, String name, Class<?> type) {
        super(getter, name, type);
    }

    @Override
    public <T extends Annotation> T getPropertyAnnotation(Class<T> type) {
        return getAnnotation(type);
    }

}
