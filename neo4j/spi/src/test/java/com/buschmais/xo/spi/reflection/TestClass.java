package com.buschmais.xo.spi.reflection;

interface TestClass {

    @TestAnnotation
    String getProperty();

    void setProperty(String property);

    String getInvalidProperty();

    @TestAnnotation
    void setInvalidProperty(String invalidProperty);
}
