package com.buschmais.cdo.impl.proxy.query.row;

import com.buschmais.cdo.impl.proxy.query.RowProxyMethod;

import java.util.Map;

public class GetColumnsMethod implements RowProxyMethod {

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        return entity.keySet();
    }

}
