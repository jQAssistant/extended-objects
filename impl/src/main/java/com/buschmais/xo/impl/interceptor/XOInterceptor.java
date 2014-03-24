package com.buschmais.xo.impl.interceptor;

public interface XOInterceptor {

    Object invoke(InvocationContext invocationContext) throws Throwable;

}
