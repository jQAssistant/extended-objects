package com.buschmais.xo.neo4j.spi.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Converter {

    private List<TypeConverter> converters;

    public Converter(List<TypeConverter> converters) {
        this.converters = converters;
    }

    public <T> T  convert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> valueType = value.getClass();
        for (TypeConverter converter : converters) {
            if (converter.getType().isAssignableFrom(valueType)) {
                return (T) converter.convert(value);
            }
        }
        if (value instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) value;
            List<Object> values = new ArrayList<>();
            for (Object o : iterable) {
                values.add(convert(o));
            }
            return (T) values;
        } else if (value instanceof Map<?, ?>) {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                result.put(convert(entry.getKey()), convert(entry.getValue()));
            }
            return (T) result;
        }
        return (T) value;
    }

}
