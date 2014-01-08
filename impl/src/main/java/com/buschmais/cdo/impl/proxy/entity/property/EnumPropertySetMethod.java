package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.method.EnumPropertyMethodMetadata;

public class EnumPropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, EnumPropertyMethodMetadata> {

    public EnumPropertySetMethod(EnumPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        Enum<?> value = (Enum<?>) args[0];
        getPropertyManager().setEnumProperty(entity, getMetadata(), value);
        return null;
    }
}
