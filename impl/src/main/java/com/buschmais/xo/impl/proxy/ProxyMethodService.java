package com.buschmais.xo.impl.proxy;

import java.lang.reflect.Method;

public interface ProxyMethodService<E> {

    Object invoke(E element, Object instance, Method method, Object[] args) throws Exception;
}
