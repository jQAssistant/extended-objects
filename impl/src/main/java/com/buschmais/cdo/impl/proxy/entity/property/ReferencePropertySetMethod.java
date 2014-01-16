package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.ReferencePropertyMethodMetadata;

public class ReferencePropertySetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Relation, ReferencePropertyMethodMetadata> {

    public ReferencePropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, ReferencePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        Entity target = value != null ? getSessionContext().getEntityInstanceManager().getDatastoreType(value) : null;
        getSessionContext().getPropertyManager().createSingleRelation(entity, getMetadata().getRelationshipMetadata(), getMetadata().getDirection(), target);
        return null;
    }
}
