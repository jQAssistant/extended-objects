package com.buschmais.cdo.neo4j.impl.query.proxy.method.object;

import com.buschmais.cdo.neo4j.impl.query.proxy.method.RowProxyMethod;

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
