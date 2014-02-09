package com.buschmais.cdo.impl.proxy.query.composite;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.proxy.ProxyMethod;

import java.util.Map;

public class AsMethod implements ProxyMethod<Map<String, Object>> {

    @Override
    public Object invoke(Map<String, Object> row, Object instance, Object[] args) throws Exception {
        if (row.size() == 1) {
            Class<?> type = (Class) args[0];
            Object value = row.values().iterator().next();
            if (value != null && !type.isAssignableFrom(value.getClass())) {
                throw new CdoException("Cannot cast value of type '" + value.getClass() + "' to '" + type + "'.");
            }
            return value;
        }
        throw new CdoException("The row contains more than one column.");
    }
}
