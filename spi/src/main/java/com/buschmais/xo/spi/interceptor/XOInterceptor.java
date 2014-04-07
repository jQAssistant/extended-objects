package com.buschmais.xo.spi.interceptor;

public interface XOInterceptor {

    Object invoke(InvocationContext invocationContext) throws Throwable;

}
