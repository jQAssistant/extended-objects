package com.buschmais.cdo.neo4j.impl.common.proxy.method;

import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

import java.lang.reflect.Method;

public interface ProxyMethodService <E, M extends ProxyMethod<?>> {

     Object invoke(E element, Object instance, Method method, Object[] args);
}
