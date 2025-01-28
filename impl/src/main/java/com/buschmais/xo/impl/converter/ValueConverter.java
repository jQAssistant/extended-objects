package com.buschmais.xo.impl.converter;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    public <T> T convert(Object value, Type propertyType) {
        return (T) doConvert(value, propertyType);
    }

    public <T> T convert(Map<String, Object> value, RowProxyMethodService<Entity, Relation> rowProxyMethodService) {
        RowInvocationHandler invocationHandler = new RowInvocationHandler(value, rowProxyMethodService);
        return sessionContext.getProxyFactory()
            .createInstance(invocationHandler, rowProxyMethodService.getCompositeType());
    }

    private Object doConvert(Object value, Type targetType) {
        if (value == null) {
            return convertNullValue(targetType);
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
            } else if (targetType instanceof Class<?>) {
                Class<?> targetClass = (Class<?>) targetType;
                if (value instanceof Map && isProjection(targetClass)) {
                    RowProxyMethodService<Entity, Relation> rowProxyMethodService = new RowProxyMethodService<>(targetClass, sessionContext);
                    return convert((Map<String, Object>) value, rowProxyMethodService);
                } else if (targetClass.isArray()) {
                    if (Collection.class.isAssignableFrom(value.getClass())) {
                        return toArray((Collection<?>) value, targetClass.getComponentType());
                    } else if (value.getClass()
                        .isArray()) {
                        return toArray(value, targetClass.getComponentType());
                    }
                } else if (Enum.class.isAssignableFrom(targetClass)) {
                    return Enum.valueOf((Class<Enum>) targetType, (String) value);
                } else if (targetClass.isPrimitive()) {
                    return convertPrimitive(value, targetClass);
                } else if (Primitives.isWrapperType(targetClass)) {
                    return convertPrimitive(value, Primitives.unwrap(targetClass));
                } else if (targetClass.isAssignableFrom(value.getClass())) {
                    return value;
                }
            } else if (targetType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) targetType;
                if (value instanceof Set<?>) {
                    Type elementType = parameterizedType.getActualTypeArguments()[0];
                    return convertIterable((Iterable<?>) value, new LinkedHashSet<>(), elementType);
                } else if (value instanceof Iterable<?>) {
                    Type elementType = parameterizedType.getActualTypeArguments()[0];
                    return convertIterable((Iterable<?>) value, new ArrayList<>(), elementType);
                } else if (value instanceof Map<?, ?>) {
                    Type keyType = parameterizedType.getActualTypeArguments()[0];
                    Type valueType = parameterizedType.getActualTypeArguments()[1];
                    return convertMap((Map<?, ?>) value, new LinkedHashMap<>(), keyType, valueType);
                }
            }
            throw new XOException("Cannot convert value '" + value + "' of type " + value.getClass() + " to " + targetType);
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

    private Object convertNullValue(Type propertyType) {
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

    private Collection<Object> convertIterable(Iterable<?> iterable, Collection<Object> decodedCollection, Type elementType) {
        for (Object o : iterable) {
            decodedCollection.add(doConvert(o, elementType));
        }
        return decodedCollection;
    }

    private Map<Object, Object> convertMap(Map<?, ?> map, Map<Object, Object> decodedMap, Type keyType, Type valueType) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            decodedMap.put(doConvert(entry.getKey(), keyType), doConvert(entry.getValue(), valueType));
        }
        return decodedMap;
    }
}
