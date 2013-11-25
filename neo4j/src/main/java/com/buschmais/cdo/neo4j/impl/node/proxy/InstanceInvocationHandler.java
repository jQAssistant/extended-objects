package com.buschmais.cdo.neo4j.impl.node.proxy;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.impl.common.proxy.method.AbstractProxyMethodService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InstanceInvocationHandler<E> implements InvocationHandler {

    private E entity;
    private AbstractProxyMethodService<E, ?> proxyMethodService;

    public InstanceInvocationHandler(E entity, AbstractProxyMethodService<E, ?> proxyMethodService) {
        this.entity = entity;
        this.proxyMethodService = proxyMethodService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (entity == null) {
            throw new CdoException("Invalid access to an un-managed instance.");
        }
        return proxyMethodService.invoke(entity, proxy, method, args);
    }

    public E getEntity() {
        return entity;
    }

    public void close() {
        entity = null;
    }
}
