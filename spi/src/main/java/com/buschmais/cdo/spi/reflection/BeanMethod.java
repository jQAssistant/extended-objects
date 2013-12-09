package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface BeanMethod {

    Method getMethod();

    <T extends Annotation> T getAnnotation(Class<T> type);
}
