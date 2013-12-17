package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface TypeMethod {

    Method getMethod();

    <T extends Annotation> T getAnnotation(Class<T> type);

    <T extends Annotation, M extends Annotation> T getByMetaAnnotation(Class<M> type);
}
