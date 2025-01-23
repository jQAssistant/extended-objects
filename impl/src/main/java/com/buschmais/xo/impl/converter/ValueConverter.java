package com.buschmais.xo.impl.converter;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.type.CompositeType;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.query.RowInvocationHandler;
import com.buschmais.xo.impl.proxy.query.RowProxyMethodService;
import com.google.common.primitives.Primitives;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ValueConverter {

    public static Object convert(Object value, Class<?> propertyType, SessionContext sessionContext) {
        if (value != null) {
            if (propertyType.isAssignableFrom(value.getClass())) {
                return value;
            } else if (Enum.class.isAssignableFrom(propertyType)) {
                return Enum.valueOf((Class<Enum>) propertyType, (String) value);
            } else if (propertyType.isArray()) {
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    return toArray((Collection<?>) value, propertyType.getComponentType(), sessionContext);
                } else if (value.getClass()
                    .isArray()) {
                    return toArray(value, propertyType.getComponentType(), sessionContext);
                }
            } else if (propertyType.isPrimitive()) {
                return convertPrimitive(value, propertyType);
            } else if (Primitives.isWrapperType(propertyType)) {
                return convertPrimitive(value, Primitives.unwrap(propertyType));
            } else if (value instanceof Map && propertyType.isInterface()) {
                RowProxyMethodService rowProxyMethodService = new RowProxyMethodService(propertyType, sessionContext);
                RowInvocationHandler invocationHandler = new RowInvocationHandler((Map<String, Object>) value, rowProxyMethodService);
                CompositeType compositeType = CompositeType.builder().type(propertyType).build();
                return sessionContext.getProxyFactory()
                    .createInstance(invocationHandler, compositeType);
            }
            throw new XOException("Cannot convert value '" + value + "' of type " + value.getClass() + " to " + propertyType);
        } else if (boolean.class.equals(propertyType)) {
            return false;
        } else if (short.class.equals(propertyType)) {
            return 0;
        } else if (int.class.equals(propertyType)) {
            return 0;
        } else if (long.class.equals(propertyType)) {
            return 0L;
        } else if (float.class.equals(propertyType)) {
            return 0f;
        } else if (double.class.equals(propertyType)) {
            return 0d;
        } else if (char.class.equals(propertyType)) {
            return 0;
        } else if (byte.class.equals(propertyType)) {
            return 0;
        }
        return null;
    }

    private static Object toArray(Object values, Class<?> componentType, SessionContext sessionContext) {
        int length = Array.getLength(values);
        Object array = Array.newInstance(componentType, length);
        for (int index = 0; index < length; index++) {
            Array.set(array, index, convert(Array.get(values, index), componentType, sessionContext));
        }
        return array;
    }

    private static Object toArray(Collection<?> values, Class<?> componentType, SessionContext sessionContext) {
        Object array = Array.newInstance(componentType, values.size());
        int index = 0;
        for (Object value : values) {
            Array.set(array, index, convert(value, componentType, sessionContext));
            index++;
        }
        return array;
    }

    private static Object convertPrimitive(Object value, Class<?> propertyType) {
        if (Number.class.isAssignableFrom(value.getClass())) {
            Number number = (Number) value;
            if (byte.class.equals(propertyType)) {
                return number.byteValue();
            } else if (short.class.equals(propertyType)) {
                return number.shortValue();
            } else if (int.class.equals(propertyType)) {
                return number.intValue();
            } else if (long.class.equals(propertyType)) {
                return number.longValue();
            } else if (float.class.equals(propertyType)) {
                return number.floatValue();
            } else if (double.class.equals(propertyType)) {
                return number.doubleValue();
            }
        } else if (String.class.isAssignableFrom(value.getClass())) {
            if (Character.class.equals(propertyType) || char.class.equals(propertyType)) {
                return ((String) value).charAt(0);
            }
        }
        return value;
    }

}
