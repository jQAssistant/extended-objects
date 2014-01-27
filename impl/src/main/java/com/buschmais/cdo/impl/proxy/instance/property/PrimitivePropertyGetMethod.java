package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.PrimitivePropertyMethodMetadata;

public class PrimitivePropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity,
        PrimitivePropertyMethodMetadata> {

    public PrimitivePropertyGetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        Object value;
        if (!getPropertyManager().hasProperty(entity, metadata)) {
            value = null;
        } else {
            value = getPropertyManager().getProperty(entity, metadata);
        }
        Class<?> type = metadata.getAnnotatedMethod().getType();
        return convert(value, type);
    }

    private Object convert(Object value, Class<?> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        } else if (value == null) {
            if (boolean.class.equals(type)) {
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
        }
        return value;
    }
}
