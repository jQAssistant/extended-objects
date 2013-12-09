package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;

public interface PropertyMethod extends BeanMethod {

    String getName();

    Class<?> getType();

    <T extends Annotation> T getPropertyAnnotation(Class<T> type);
}
