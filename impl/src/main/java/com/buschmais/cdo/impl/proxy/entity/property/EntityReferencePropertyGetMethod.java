package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod;
import com.buschmais.cdo.spi.metadata.method.EntityReferencePropertyMethodMetadata;

public class EntityReferencePropertyGetMethod<Entity, Relation> extends AbstractPropertyMethod<Entity, Entity, Relation, EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertyGetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, EntityReferencePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Entity, Entity, Relation> getPropertyManager() {
        return getSessionContext().getEntityPropertyManager();
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        return getSessionContext().getEntityPropertyManager().getEntityReference(entity, getMetadata());
    }
}