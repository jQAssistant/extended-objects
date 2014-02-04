package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.EntityPropertyManager;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.RelationReferencePropertyMethodMetadata;

public class RelationReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, EntityPropertyManager<Entity, Relation>, RelationReferencePropertyMethodMetadata> {

    public RelationReferencePropertyGetMethod(EntityPropertyManager<Entity, Relation> propertyManager, RelationReferencePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        return getPropertyManager().getRelationReference(entity, getMetadata());
    }
}