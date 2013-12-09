package com.buschmais.cdo.neo4j.impl.common.reflection;

import java.lang.annotation.Annotation;

public interface PropertyMethod extends BeanMethod {

    String getName();

    Class<?> getType();

    <T extends Annotation> T getPropertyAnnotation(Class<T> type);
}
