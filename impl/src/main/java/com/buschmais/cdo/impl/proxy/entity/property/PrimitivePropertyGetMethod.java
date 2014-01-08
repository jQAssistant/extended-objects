package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.method.PrimitivePropertyMethodMetadata;

public class PrimitivePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation,
        PrimitivePropertyMethodMetadata> {

    public PrimitivePropertyGetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        if (!getPropertyManager().hasProperty(entity, metadata)) {
            return null;
        }

        Object value = getPropertyManager().getProperty(entity, metadata);
        Class<?> type = metadata.getAnnotatedMethod().getType();
        if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        return value;
    }
}
