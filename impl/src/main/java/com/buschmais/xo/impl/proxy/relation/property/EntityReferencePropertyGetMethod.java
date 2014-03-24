package com.buschmais.xo.impl.proxy.relation.property;

import com.buschmais.xo.impl.RelationPropertyManager;
import com.buschmais.xo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.xo.spi.metadata.method.EntityReferencePropertyMethodMetadata;

public class EntityReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Relation, RelationPropertyManager<Entity, Relation>, EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertyGetMethod(RelationPropertyManager<Entity, Relation> propertyManager, EntityReferencePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Relation relation, Object instance, Object[] args) {
        return getPropertyManager().getEntityReference(relation, getMetadata());
    }
}