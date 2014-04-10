package com.buschmais.xo.spi.interceptor;

public interface XOInterceptor {

    boolean isActive();

    Object invoke(InvocationContext invocationContext) throws Throwable;

}
