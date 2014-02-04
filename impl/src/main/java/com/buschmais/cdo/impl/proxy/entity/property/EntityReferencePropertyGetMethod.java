package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.EntityPropertyManager;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.EntityReferencePropertyMethodMetadata;

public class EntityReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, EntityPropertyManager<Entity, Relation>, EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertyGetMethod(EntityPropertyManager<Entity, Relation> propertyManager, EntityReferencePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        return getPropertyManager().getEntityReference(entity, getMetadata());
    }
}