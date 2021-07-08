package com.buschmais.xo.impl.proxy.common.property;

import java.lang.reflect.Array;
import java.util.Collection;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.metadata.method.PrimitivePropertyMethodMetadata;
import com.buschmais.xo.impl.AbstractPropertyManager;

public abstract class AbstractPrimitivePropertyGetMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType>>
        extends AbstractPropertyMethod<DatastoreType, PropertyManager, PrimitivePropertyMethodMetadata> {

    public AbstractPrimitivePropertyGetMethod(PropertyManager propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    @Override
    public Object invoke(DatastoreType datastoreType, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        PropertyManager propertyManager = getPropertyManager();
        Object value;
        if (!propertyManager.hasProperty(datastoreType, metadata)) {
            value = null;
        } else {
            value = propertyManager.getProperty(datastoreType, metadata);
        }
        return convert(value, metadata.getAnnotatedMethod().getType());
    }

    private Object convert(Object value, Class<?> propertyType) {
        if (value != null) {
            if (propertyType.isAssignableFrom(value.getClass())) {
                return value;
            } else if (Enum.class.isAssignableFrom(propertyType)) {
                return Enum.valueOf((Class<Enum>) propertyType, (String) value);
            } else if (propertyType.isArray()) {
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    return toArray((Collection<?>) value, propertyType.getComponentType());
                }
            } else if (propertyType.isPrimitive()) {
                return convertPrimitive(value, propertyType);
            }
            throw new XOException("Cannot convert value of type " + value.getClass() + " to type " + propertyType);
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

    private Object toArray(Collection<?> values, Class<?> componentType) {
        Object array = Array.newInstance(componentType, values.size());
        int index = 0;
        for (Object value : values) {
            Array.set(array, index, convert(value, componentType));
            index++;
        }
        return array;
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

}
