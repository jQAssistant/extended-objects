package com.buschmais.xo.impl.proxy.query.property;

import java.lang.reflect.Type;
import java.util.Map;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.converter.ValueConverter;
import com.buschmais.xo.impl.proxy.query.RowProxyMethod;

public class GetMethod<Entity, Relation> implements RowProxyMethod {

    private final String name;
    private final Type type;
    private final ValueConverter<Entity, Relation> valueConverter;

    public GetMethod(String name, Type type, SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext) {
        this.name = name;
        this.type = type;
        this.valueConverter = new ValueConverter<>(sessionContext);
    }

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        if (!entity.containsKey(name)) {
            throw new XOException("Query result does not contain column '" + name + "'");
        }
        Object value = entity.get(name);
        return valueConverter.convert(value, type);
    }
}
