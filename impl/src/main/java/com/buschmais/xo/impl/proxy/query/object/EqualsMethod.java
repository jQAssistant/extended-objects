package com.buschmais.xo.impl.proxy.query.object;

import com.buschmais.xo.impl.proxy.query.RowProxyMethod;

import java.util.Map;

public class EqualsMethod implements RowProxyMethod {

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        Object other = args[0];
        if (other != null && other instanceof Map) {
            return entity.equals(other);
        }
        return false;
    }
}
