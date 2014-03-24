package com.buschmais.xo.impl.proxy.entity.property;

import com.buschmais.xo.impl.EntityPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.xo.spi.metadata.method.RelationReferencePropertyMethodMetadata;

public class RelationReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, EntityPropertyManager<Entity, Relation>, RelationReferencePropertyMethodMetadata> {

    public RelationReferencePropertyGetMethod(EntityPropertyManager<Entity, Relation> propertyManager, RelationReferencePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        return getPropertyManager().getRelationReference(entity, getMetadata());
    }
}