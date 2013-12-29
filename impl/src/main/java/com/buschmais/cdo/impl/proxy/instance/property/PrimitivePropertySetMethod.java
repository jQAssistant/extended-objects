package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.PrimitivePropertyMethodMetadata;

public class PrimitivePropertySetMethod<Entity> extends AbstractPropertyMethod<Entity, PrimitivePropertyMethodMetadata> {

    public PrimitivePropertySetMethod(PrimitivePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        PrimitivePropertyMethodMetadata<?> metadata = getMetadata();
        if (value != null) {
            if (Enum.class.isAssignableFrom(metadata.getAnnotatedMethod().getType())) {
                value = ((Enum) value).name();
            }
            getPropertyManager().setProperty(entity, metadata, value);
        } else {
            if (getPropertyManager().hasProperty(entity, metadata)) {
                getPropertyManager().removeProperty(entity, metadata);
            }
        }
        return null;
    }
}
