package com.buschmais.cdo.impl.proxy.relation.property;

import com.buschmais.cdo.impl.RelationPropertyManager;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.EntityReferencePropertyMethodMetadata;

public class EntityReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Relation, RelationPropertyManager<Entity, Relation>, EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertyGetMethod(RelationPropertyManager<Entity, Relation> propertyManager, EntityReferencePropertyMethodMetadata metadata) {
        super(propertyManager, metadata);
    }

    public Object invoke(Relation relation, Object instance, Object[] args) {
        return getPropertyManager().getEntityReference(relation, getMetadata());
    }
}