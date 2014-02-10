package com.buschmais.cdo.impl.proxy.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class RowInvocationHandler implements InvocationHandler {

    private final Map<String, Object> row;

    private final RowProxyMethodService rowProxyMethodService;

    public RowInvocationHandler(Map<String, Object> row, RowProxyMethodService rowProxyMethodService) {
        this.row = row;
        this.rowProxyMethodService = rowProxyMethodService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return rowProxyMethodService.invoke(row, proxy, method, args);
    }
}
