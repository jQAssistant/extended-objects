package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.PropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.ReferencePropertyMethodMetadata;
import com.buschmais.cdo.spi.metadata.type.RelationTypeMetadata;

public class ReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, ReferencePropertyMethodMetadata> {

    public ReferencePropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, ReferencePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        RelationTypeMetadata relationshipMetadata = getMetadata().getRelationshipMetadata();
        PropertyManager<?, Entity, ?, Relation> propertyManager = getSessionContext().getPropertyManager();
        Relation relation = propertyManager.getSingleRelation(entity, relationshipMetadata, getMetadata().getDirection());
        if (relation != null) {
            if (relationshipMetadata.getAnnotatedType() != null) {
                return getSessionContext().getRelationInstanceManager().getInstance(relation);
            } else {
                Entity target = propertyManager.getRelativeTarget(relation, getMetadata().getDirection());
                return getSessionContext().getEntityInstanceManager().getInstance(target);
            }
        }
        return null;
    }
}