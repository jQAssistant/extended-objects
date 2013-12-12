package com.buschmais.cdo.impl.proxy.interceptor;

import java.lang.reflect.InvocationHandler;

public interface CdoInterceptor<T>  extends InvocationHandler {

    T getDelegate();
}
