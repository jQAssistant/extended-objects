package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.EnumPropertyMethodMetadata;

public class EnumPropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity, EnumPropertyMethodMetadata> {

    public EnumPropertyGetMethod(EnumPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return getPropertyManager().getEnumProperty(entity, getMetadata());
    }
}
