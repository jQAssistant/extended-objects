package com.buschmais.xo.impl.proxy.query.object;

import java.util.Map;

import com.buschmais.xo.impl.proxy.query.RowProxyMethod;

public class HashCodeMethod implements RowProxyMethod {

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        return entity.hashCode();
    }
}
