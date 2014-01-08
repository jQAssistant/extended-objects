package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.InstanceManager;
import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.spi.metadata.method.AbstractMethodMetadata;
import com.buschmais.cdo.spi.metadata.method.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

public class ReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, ReferencePropertyMethodMetadata> {

    public ReferencePropertyGetMethod(ReferencePropertyMethodMetadata metadata, InstanceManager<?, Entity, ?, ?, Relation, ?> instanceManager, PropertyManager<?, Entity, ?, Relation> propertyManager) {
        super(metadata, instanceManager, propertyManager);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        RelationTypeMetadata relationshipMetadata = getMetadata().getRelationshipMetadata();
        Relation relation = getPropertyManager().getSingleRelation(entity, relationshipMetadata, getMetadata().getDirection());
        if (relation != null) {
            if (relationshipMetadata.getAnnotatedType() != null) {
                return getInstanceManager().getRelationInstance(relation);
            } else {
                Entity target = getPropertyManager().getRelativeTarget(relation, getMetadata().getDirection());
                return getInstanceManager().getEntityInstance(target);
            }
        }
        return null;
    }
}