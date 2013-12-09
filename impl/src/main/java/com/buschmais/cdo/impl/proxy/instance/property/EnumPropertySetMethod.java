package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.EnumPropertyMethodMetadata;

public class EnumPropertySetMethod<Entity> extends AbstractPropertyMethod<Entity, EnumPropertyMethodMetadata> {

    public EnumPropertySetMethod(EnumPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        getPropertyManager().setEnumProperty(entity, getMetadata(), value);
        return null;
    }
}
