package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.method.EnumPropertyMethodMetadata;

public class EnumPropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, EnumPropertyMethodMetadata> {

    public EnumPropertyGetMethod(EnumPropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    @Override
    public Object invoke(Entity entity, Object instance, Object[] args) {
        return getPropertyManager().getEnumProperty(entity, getMetadata());
    }
}
