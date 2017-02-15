package com.buschmais.xo.neo4j.remote.impl.converter;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.remote.impl.model.AbstractRemotePropertyContainer;
import com.buschmais.xo.neo4j.spi.helper.TypeConverter;

public class RemoteParameterConverter implements TypeConverter {

    @Override
    public Class<?> getType() {
        return AbstractRemotePropertyContainer.class;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof AbstractRemotePropertyContainer) {
            return ((AbstractRemotePropertyContainer) value).getId();
        }
        throw new XOException("Unsupported value " + value);
    }
}
