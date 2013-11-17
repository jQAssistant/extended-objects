package com.buschmais.cdo.neo4j.impl.query.proxy.method.object;

import com.buschmais.cdo.neo4j.impl.query.proxy.method.RowProxyMethod;

import java.util.Map;

public class EqualsMethod implements RowProxyMethod {

    @Override
    public Object invoke(Map<String, Object> element, Object instance, Object[] args) {
        Object other = args[0];
        if (other != null && other instanceof Map) {
            return element.equals(other);
        }
        return false;
    }
}
