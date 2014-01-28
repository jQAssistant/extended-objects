package com.buschmais.cdo.impl.proxy.entity.property;

import com.buschmais.cdo.impl.AbstractPropertyManager;
import com.buschmais.cdo.impl.EntityPropertyManager;
import com.buschmais.cdo.impl.SessionContext;
import com.buschmais.cdo.spi.metadata.method.CollectionPropertyMethodMetadata;

import java.util.Collection;

public class EntityCollectionPropertySetMethod<Entity, Relation> extends com.buschmais.cdo.impl.proxy.common.property.AbstractPropertyMethod<Entity,Entity,Relation,CollectionPropertyMethodMetadata> {

    public EntityCollectionPropertySetMethod(SessionContext<?, Entity, ?, ?, ?, Relation, ?, ?> sessionContext, CollectionPropertyMethodMetadata metadata) {
        super(sessionContext, metadata);
    }

    @Override
    protected AbstractPropertyManager<Entity, Entity, Relation> getPropertyManager() {
        return getSessionContext().getEntityPropertyManager();
    }

    public Object invoke(Entity entity, Object instance, Object[] args) {
        EntityPropertyManager<Entity,Relation> propertyManager = getSessionContext().getEntityPropertyManager();
        propertyManager.removeEntityReferences(entity, getMetadata());
        Collection<?> collection = (Collection<?>) args[0];
        for (Object o : collection) {
            propertyManager.createEntityReference(entity, getMetadata(), o);
        }
        return null;
    }
}
