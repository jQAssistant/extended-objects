package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.method.ReferencePropertyMethodMetadata;

public class ReferencePropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, ReferencePropertyMethodMetadata> {

    public ReferencePropertySetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager instanceManager, PropertyManager propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        Entity target = value != null ? getInstanceManager().getEntity(value) : null;
        getPropertyManager().createSingleRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), target);
        return null;
    }
}
