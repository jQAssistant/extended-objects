package com.buschmais.xo.impl.proxy.common.property;

import java.lang.reflect.Array;
import java.util.Collection;

import com.buschmais.xo.api.XOException;
import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

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

    private Object convert(Object value, Class<?> type) {
        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                return value;
            } else if (Enum.class.isAssignableFrom(type)) {
                return Enum.valueOf((Class<Enum>) type, (String) value);
            } else if (type.isArray()) {
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    return toArray((Collection<?>) value, type.getComponentType());
                }
            }
            throw new XOException("Cannot convert value of type " + value.getClass() + " to type " + type);
        } else if (boolean.class.equals(type)) {
            return false;
        } else if (short.class.equals(type)) {
            return 0;
        } else if (int.class.equals(type)) {
            return 0;
        } else if (long.class.equals(type)) {
            return 0L;
        } else if (float.class.equals(type)) {
            return 0f;
        } else if (double.class.equals(type)) {
            return 0d;
        } else if (char.class.equals(type)) {
            return 0;
        } else if (byte.class.equals(type)) {
            return 0;
        }
        return null;
    }

    private <T> T[] toArray(Collection<?> value, Class<T> componentType) {
        T[] array = (T[]) Array.newInstance(componentType, 0);
        return value.toArray(array);
    }

}
