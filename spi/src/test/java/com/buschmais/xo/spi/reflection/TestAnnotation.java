package com.buschmais.xo.spi.reflection;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@TestMetaAnnotation
@Retention(RUNTIME)
public @interface TestAnnotation {
}
