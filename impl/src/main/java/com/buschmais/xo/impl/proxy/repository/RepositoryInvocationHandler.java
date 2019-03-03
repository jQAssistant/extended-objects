package com.buschmais.xo.impl.proxy.repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.buschmais.xo.api.XOManager;

public class RepositoryInvocationHandler implements InvocationHandler {

    private RepositoryProxyMethodService proxyMethodService;

    private XOManager xoManager;

    public RepositoryInvocationHandler(RepositoryProxyMethodService proxyMethodService, XOManager xoManager) {
        this.proxyMethodService = proxyMethodService;
        this.xoManager = xoManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proxyMethodService.invoke(xoManager, proxy, method, args);
    }
}
