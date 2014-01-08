package com.buschmais.cdo.impl.proxy.entity;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.impl.proxy.ProxyMethodService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InstanceInvocationHandler<DatastoreType> implements InvocationHandler {

    private DatastoreType datastoreType;
    private ProxyMethodService<DatastoreType, ?> proxyMethodService;

    public InstanceInvocationHandler(DatastoreType datastoreType, ProxyMethodService<DatastoreType, ?> proxyMethodService) {
        this.datastoreType = datastoreType;
        this.proxyMethodService = proxyMethodService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (datastoreType == null) {
            throw new CdoException("Invalid access to an un-managed instance.");
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
