package com.buschmais.xo.impl.proxy.query.row;

import java.util.Map;

import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.converter.ValueConverter;
import com.buschmais.xo.impl.proxy.query.RowProxyMethod;

public class GetMethod<Entity, Relation> implements RowProxyMethod {

    private final ValueConverter<Entity, Relation> valueConverter;

    public GetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.valueConverter = new ValueConverter<>(sessionContext);
    }

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        String column = (String) args[0];
        Class<?> type = (Class<?>) args[1];
        Object value = entity.get(column);
        return valueConverter.convert(value, type);
    }

}
