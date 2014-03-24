package com.buschmais.xo.impl.proxy.query.property;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.proxy.query.RowProxyMethod;

import java.util.Map;

public class GetMethod implements RowProxyMethod {

    private final String name;
    private final Class<?> type;

    public GetMethod(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        if (!entity.containsKey(name)) {
            throw new XOException("Query result does not contain column '" + name + "'");
        }
        Object value = entity.get(name);
        return value != null ? type.cast(value) : null;
    }
}
