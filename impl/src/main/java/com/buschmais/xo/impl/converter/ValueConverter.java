package com.buschmais.xo.impl.converter;

import java.lang.reflect.Array;
import java.util.*;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.SessionContext;
import com.buschmais.xo.impl.proxy.query.RowInvocationHandler;
import com.buschmais.xo.impl.proxy.query.RowProxyMethodService;

import com.google.common.primitives.Primitives;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ValueConverter<Entity, Relation> {

    private final SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?, ?> sessionContext;

    public boolean isProjection(Class<?> returnType) {
        return !returnType.isAssignableFrom(Map.class) && returnType.isInterface();
    }

    public boolean isTypedQuery(Class<?> returnType) {
        return sessionContext.getMetadataProvider()
            .getQuery(returnType) != null;
    }

    public <T> T convert(Object value, Class<?> propertyType) {
        return (T) doConvert(value, propertyType);
    }

    public <T> T convert(Map<String, Object> value, RowProxyMethodService rowProxyMethodService) {
        RowInvocationHandler invocationHandler = new RowInvocationHandler(value, rowProxyMethodService);
        return sessionContext.getProxyFactory()
            .createInstance(invocationHandler, rowProxyMethodService.getCompositeType());
    }

    private Object doConvert(Object value, Class<?> returnType) {
        if (value == null) {
            return convertNullValue(returnType);
        } else {
            if (sessionContext.getDatastoreSession()
                .getDatastoreEntityManager()
                .isEntity(value)) {
                return sessionContext.getEntityInstanceManager()
                    .readInstance((Entity) value);
            } else if (sessionContext.getDatastoreSession()
                .getDatastoreRelationManager()
                .isRelation(value)) {
                return sessionContext.getRelationInstanceManager()
                    .readInstance((Relation) value);
            } else if (value instanceof Map<?, ?>) {
                if (isProjection(returnType)) {
                    RowProxyMethodService rowProxyMethodService = new RowProxyMethodService(returnType, sessionContext);
                    return convert((Map<String, Object>) value, rowProxyMethodService);
                } else {
                    return convertMap((Map<?, ?>) value, new LinkedHashMap<>());
                }
            } else if (returnType.isArray()) {
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    return toArray((Collection<?>) value, returnType.getComponentType());
                } else if (value.getClass()
                    .isArray()) {
                    return toArray(value, returnType.getComponentType());
                }
            } else if (value instanceof Set<?>) {
                return convertIterable((Iterable<?>) value, new LinkedHashSet<>());
            } else if (value instanceof Iterable<?>) {
                return convertIterable((Iterable<?>) value, new ArrayList<>());
            } else if (Enum.class.isAssignableFrom(returnType)) {
                return Enum.valueOf((Class<Enum>) returnType, (String) value);
            } else if (returnType.isPrimitive()) {
                return convertPrimitive(value, returnType);
            } else if (Primitives.isWrapperType(returnType)) {
                return convertPrimitive(value, Primitives.unwrap(returnType));
            } else if (returnType.isAssignableFrom(value.getClass())) {
                return value;
            }
            throw new XOException("Cannot convert value '" + value + "' of type " + value.getClass() + " to " + returnType);
        }
    }

    private Object toArray(Object values, Class<?> returnType) {
        int length = Array.getLength(values);
        Object array = Array.newInstance(returnType, length);
        for (int index = 0; index < length; index++) {
            Array.set(array, index, doConvert(Array.get(values, index), returnType));
        }
        return array;
    }

    private Object toArray(Collection<?> values, Class<?> returnType) {
        Object array = Array.newInstance(returnType, values.size());
        int index = 0;
        for (Object value : values) {
            Array.set(array, index, doConvert(value, returnType));
            index++;
        }
        return array;
    }

    private Object convertNullValue(Class<?> propertyType) {
        if (boolean.class.equals(propertyType)) {
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

    private Object convertPrimitive(Object value, Class<?> propertyType) {
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

    private Collection<Object> convertIterable(Iterable<?> iterable, Collection<Object> decodedCollection) {
        for (Object o : iterable) {
            decodedCollection.add(convertElement(o));
        }
        return decodedCollection;
    }

    private Map<Object, Object> convertMap(Map<?, ?> map, Map<Object, Object> decodedMap) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            decodedMap.put(convertElement(entry.getKey()), convertElement(entry.getValue()));
        }
        return decodedMap;
    }

    private Object convertElement(Object value) {
        return doConvert(value, value.getClass());
    }
}
