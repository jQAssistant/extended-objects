package com.buschmais.cdo.impl.interceptor;

public interface CdoInterceptor {

    Object invoke(InvocationContext invocationContext) throws Throwable;

}
