package com.buschmais.xo.impl.proxy.query.property;

import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.converter.ValueConverter;
import com.buschmais.xo.impl.proxy.query.RowProxyMethod;
import lombok.RequiredArgsConstructor;

public class GetMethod implements RowProxyMethod {

    private final SessionContext<?, ?, ?, ?, ?, ?, ?, ?, ?> sessionContext;

    private final String name;
    private final Class<?> type;

    public GetMethod(String name, Class<?> type, SessionContext<?, ?, ?, ?, ?, ?, ?, ?, ?> sessionContext) {
        this.name = name;
        this.type = type;
        this.sessionContext = sessionContext;
    }

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        if (!entity.containsKey(name)) {
            throw new XOException("Query result does not contain column '" + name + "'");
        }
        Object value = entity.get(name);
        return ValueConverter.convert(value, type, sessionContext);
    }
}
