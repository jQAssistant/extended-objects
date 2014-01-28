package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.EntityReferencePropertyMethodMetadata;

public class EntityReferencePropertySetMethod<Entity, Relation> extends com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod<Entity,Entity,Relation,EntityReferencePropertyMethodMetadata> {

    public EntityReferencePropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, EntityReferencePropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Entity, Entity, Relation> getPropertyManager() {
        return getSessionContext().getEntityPropertyManager();
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        Object value = args[0];
        getSessionContext().getEntityPropertyManager().createEntityReference(entity, getMetadata(), value);
        return null;
    }
}
