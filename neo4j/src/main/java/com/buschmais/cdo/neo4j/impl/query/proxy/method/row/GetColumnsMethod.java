package com.buschmais.cdo.neo4j.impl.query.proxy.method.row;

import com.buschmais.cdo.neo4j.impl.query.proxy.method.RowProxyMethod;

import java.util.Map;

public class GetColumnsMethod implements RowProxyMethod {

    @Override
    public Object invoke(Map<String, Object> element, Object instance, Object[] args) {
        return element.keySet();
    }

}
