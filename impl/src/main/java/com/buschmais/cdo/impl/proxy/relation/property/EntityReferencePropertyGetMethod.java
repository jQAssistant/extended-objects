package com.buschmais.cdo.impl.proxy.relation.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.EntityReferencePropertyMethodMetadata;

public class EntityReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Relation, Entity, Relation, EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, EntityReferencePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Relation, Entity, Relation> getPropertyManager() {
        return getSessionContext().getRelationPropertyManager();
    }

    public Object invoke(Relation relation, Object instance, Object[] args) {
        return getSessionContext().getRelationPropertyManager().getEntityReference(relation, getMetadata());
    }
}