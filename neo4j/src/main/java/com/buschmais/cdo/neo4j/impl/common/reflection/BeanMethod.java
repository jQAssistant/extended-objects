package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface BeanMethod {

    Method getMethod();

    public <T extends Annotation> T getAnnotation(Class<T> type);

}
