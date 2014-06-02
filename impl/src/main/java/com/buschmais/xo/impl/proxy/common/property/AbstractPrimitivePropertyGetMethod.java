package com.buschmais.xo.impl.proxy.common.property;

import com.buschmais.xo.impl.AbstractPropertyManager;
import com.buschmais.xo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public abstract class AbstractPrimitivePropertyGetMethod<DatastoreType, PropertyManager extends AbstractPropertyManager<DatastoreType>> extends AbstractPropertyMethod<DatastoreType, PropertyManager, PrimitivePropertyMethodMetadata> {

    public AbstractPrimitivePropertyGetMethod(PropertyManager propertyManager, PrimitivePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

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
            if (Enum.class.isAssignableFrom(type)) {
                return Enum.valueOf((Class<Enum>) type, (String) value);
            }
            return value;
        } else if (boolean.class.equals(type)) {
            return false;
        } else if (short.class.equals(type)) {
            return 0;
        } else if (int.class.equals(type)) {
            return 0;
        } else if (long.class.equals(type)) {
            return 0l;
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

}
