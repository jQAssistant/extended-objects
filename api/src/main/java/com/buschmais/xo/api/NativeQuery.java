package com.buschmais.xo.api;

import java.lang.annotation.Annotation;

public interface NativeQuery<Class extends Annotation> {

    String getExpression();

}
