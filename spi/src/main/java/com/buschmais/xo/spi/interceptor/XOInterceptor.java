package com.buschmais.xo.spi.interceptor;

public interface XOInterceptor {

    boolean isActive();

    @SuppressWarnings("squid:S00112")
    Object invoke(InvocationContext invocationContext) throws Throwable;

}
