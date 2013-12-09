package com.buschmais.cdo.impl.proxy.instance.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.ReferencePropertyMethodMetadata;

public class ReferencePropertyGetMethod<Entity> extends AbstractPropertyMethod<Entity, ReferencePropertyMethodMetadata> {

    public ReferencePropertyGetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Entity target = getPropertyManager().getSingleRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection());
        return target != null ? getInstanceManager().getInstance(target) : null;
    }
}
