package com.buschmais.xo.spi.reflection;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@TestMetaAnnotation
@Retention(RUNTIME)
public @interface TestAnnotation {
}
