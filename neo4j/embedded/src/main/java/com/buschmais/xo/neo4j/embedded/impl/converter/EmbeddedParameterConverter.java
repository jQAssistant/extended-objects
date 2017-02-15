package com.buschmais.xo.neo4j.embedded.impl.converter;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.neo4j.embedded.impl.model.AbstractEmbeddedPropertyContainer;
import com.buschmais.xo.neo4j.spi.helper.TypeConverter;

public class EmbeddedParameterConverter implements TypeConverter {

    @Override
    public Class<?> getType() {
        return AbstractEmbeddedPropertyContainer.class;
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof AbstractEmbeddedPropertyContainer) {
            return ((AbstractEmbeddedPropertyContainer) value).getId();
        }
        throw new XOException("Unsupported value " + value);
    }
}
