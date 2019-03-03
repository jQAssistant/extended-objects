package com.buschmais.xo.impl.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.buschmais.xo.api.XOException;

public class InstanceInvocationHandler<DatastoreType> implements InvocationHandler {

    private DatastoreType datastoreType;
    private final ProxyMethodService<DatastoreType> proxyMethodService;

    public InstanceInvocationHandler(DatastoreType datastoreType, ProxyMethodService<DatastoreType> proxyMethodService) {
        this.datastoreType = datastoreType;
        this.proxyMethodService = proxyMethodService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (datastoreType == null) {
            throw new XOException("Invalid access to an un-managed instance.");
        }
        return proxyMethodService.invoke(datastoreType, proxy, method, args);
    }

    public DatastoreType getDatastoreType() {
        return datastoreType;
    }

    public void close() {
        datastoreType = null;
    }
}
