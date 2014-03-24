package com.buschmais.xo.impl.interceptor;

public interface CdoInterceptor {

    Object invoke(InvocationContext invocationContext) throws Throwable;

}
